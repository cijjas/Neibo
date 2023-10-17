package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.UserRole;

import java.util.List;

class DaoUtils {

    static void appendCommonConditions(StringBuilder query, List<Object> queryParams, String channel, long userId, long neighborhoodId, List<String> tags, PostStatus postStatus) {
        appendInitialWhereClause(query);
        appendNeighborhoodIdCondition(query, queryParams, neighborhoodId);

        if (channel != null && !channel.isEmpty())
            appendChannelCondition(query, queryParams, channel);

        if (userId != 0)
            appendUserIdCondition(query, queryParams, userId);

        if (tags != null && !tags.isEmpty())
            appendTagsCondition(query, queryParams, tags);

        switch (postStatus) {
            case hot:
                appendHotClause(query);
                break;
            case trending:
                appendTrendingClause(query);
                break;
            case none:
                break;
        }
    }

    static void appendCommonWorkerConditions(StringBuilder query, List<Object> queryParams, long neighborhoodId, List<String> professions) {
        appendInitialWhereClause(query);
        appendWorkerNeighborhoodIdCondition(query, queryParams, neighborhoodId);

        if(professions != null && !professions.isEmpty()) {
            appendProfessionsCondition(query, queryParams, professions);
        }
    }

    static void appendInitialWhereClause(StringBuilder query) {
        query.append(" WHERE 1 = 1");
    }

    static void appendChannelCondition(StringBuilder query, List<Object> queryParams, String channel) {
        query.append(" AND channel LIKE ?");
        queryParams.add(channel);
    }

    static void appendRoleCondition(StringBuilder query, List<Object> queryParams, UserRole role) {
        query.append(" AND u.role = ?");
        queryParams.add(role.toString());
    }


    static void appendUserIdCondition(StringBuilder query, List<Object> queryParams, long userId) {
        query.append(" AND u.userid = ?");
        queryParams.add(userId);
    }

    static void appendNeighborhoodIdCondition(StringBuilder query, List<Object> queryParams, long neighborhoodId) {
        query.append(" AND u.neighborhoodid = ?");
        queryParams.add(neighborhoodId);
    }

    static void appendWorkerNeighborhoodIdCondition(StringBuilder query, List<Object> queryParams, long neighborhoodId) {
        query.append(" AND wn.neighborhoodid = ?");
        queryParams.add(neighborhoodId);
    }

    static void appendProfessionsCondition(StringBuilder query, List<Object> queryParams, List<String> professions) {
        query.append(" AND EXISTS (");
        query.append("SELECT 1 FROM workers_professions wp JOIN professions p ON wp.professionid = p.professionid");
        query.append(" WHERE wp.workerid = w.userid AND p.profession IN (");

        appendParams(query, queryParams, professions);

        query.append(")");
        query.append(" HAVING COUNT(DISTINCT wp.professionid) = ?)"); // Ensure all specified professions exist
        queryParams.add(professions.size());
    }

    static void appendTagsCondition(StringBuilder query, List<Object> queryParams, List<String> tags) {
        query.append(" AND EXISTS (");
        query.append("SELECT 1 FROM posts_tags pt JOIN tags t ON pt.tagid = t.tagid");
        query.append(" WHERE pt.postid = p.postid AND t.tag IN (");

        appendParams(query, queryParams, tags);

        query.append(")");
        query.append(" HAVING COUNT(DISTINCT pt.tagid) = ?)"); // Ensure all specified tags exist
        queryParams.add(tags.size());
    }

    static void appendParams(StringBuilder query, List<Object> queryParams, List<String> tags) {
        //third parameter will either be tags or professions
        for (int i = 0; i < tags.size(); i++) {
            query.append("?");
            queryParams.add(tags.get(i)); // Use the tag/profession name as a string
            if (i < tags.size() - 1) {
                query.append(", ");
            }
        }
    }

    static void appendPaginationClause(StringBuilder query, List<Object> queryParams, int page, int size) {
        // Calculate the offset based on the page and size
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
}
