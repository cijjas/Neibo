package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.enums.WorkerStatus;

import java.util.List;

class DaoUtils {
    static void appendCommonConditions(StringBuilder query, List<Object> queryParams, Long channelId, Long userId, long neighborhoodId, List<Long> tags, Long postStatusId) {
        appendInitialWhereClause(query);
        appendNeighborhoodIdCondition(query, queryParams, neighborhoodId);

        if (channelId != null)
            appendChannelCondition(query, queryParams, channelId);

        if (userId != null)
            appendUserIdCondition(query, queryParams, userId);

        if (tags != null && !tags.isEmpty())
            appendTagsCondition(query, queryParams, tags);

        if (postStatusId != null) {
            switch (PostStatus.fromId(postStatusId)) {
                case HOT:
                    appendHotClause(query);
                    break;
                case TRENDING:
                    appendTrendingClause(query);
                    break;
                case NONE:
                    break;
            }
        }
    }

    static void appendInitialWhereClause(StringBuilder query) {
        query.append(" WHERE 1 = 1");
    }

    static void appendChannelCondition(StringBuilder query, List<Object> queryParams, Long channelId) {
        query.append(" AND c.channelId = ?");
        queryParams.add(channelId);
    }

    static void appendUserIdCondition(StringBuilder query, List<Object> queryParams, long userId) {
        query.append(" AND u.userid = ?");
        queryParams.add(userId);
    }

    static void appendNeighborhoodIdCondition(StringBuilder query, List<Object> queryParams, long neighborhoodId) {
        query.append(" AND u.neighborhoodid = ?");
        queryParams.add(neighborhoodId);
    }

    static void appendTagsCondition(StringBuilder query, List<Object> queryParams, List<Long> tagIds) {
        query.append(" AND EXISTS (");
        query.append("SELECT 1 FROM posts_tags pt JOIN tags t ON pt.tagid = t.tagid");
        query.append(" WHERE pt.postid = p.postid AND t.tagid IN (");

        appendLongParams(query, queryParams, tagIds);

        query.append(")");
        query.append(" HAVING COUNT(DISTINCT pt.tagid) = ?)"); // Ensure all specified tags exist
        queryParams.add(tagIds.size());
    }


    static void appendLongParams(StringBuilder query, List<Object> queryParams, List<Long> tagIds) {
        for (int i = 0; i < tagIds.size(); i++) {
            query.append("?");
            queryParams.add(tagIds.get(i));
            if (i < tagIds.size() - 1) {
                query.append(", ");
            }
        }
    }

    static void appendPaginationClause(StringBuilder query, List<Object> queryParams, int page, int size) {
        int offset = (page - 1) * size;
        query.append(" LIMIT ? OFFSET ?");
        queryParams.add(size);
        queryParams.add(offset);
    }

    static void appendHotClause(StringBuilder query) {
        query.append(" AND (SELECT COUNT(userid) FROM posts_users_likes as pul WHERE pul.postid = p.postid AND pul.likedate >= NOW() - INTERVAL '240 HOURS') >= 3");
    }

    static void appendTrendingClause(StringBuilder query) {
        query.append(" AND (SELECT COUNT(userid) FROM posts_users_likes as pul WHERE pul.postid = p.postid AND pul.likedate >= NOW() - INTERVAL '72 HOURS') >= 1");
        query.append(" AND (SELECT COUNT(commentid) FROM comments as cm WHERE cm.postid = p.postid AND cm.commentdate >= NOW() - INTERVAL '72 HOURS') >= 1");
    }

    static void appendDateClause(StringBuilder query) {
        query.append(" ORDER BY postdate DESC");
    }
}
