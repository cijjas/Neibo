package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.enums.WorkerRole;
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

        if (postStatusId != null ){
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

    static void appendCommonWorkerConditions(StringBuilder query, List<Object> queryParams, long[] neighborhoodIds, List<String> professions, String workerRole, String workerStatus) {
        appendInitialWhereClause(query);
        appendWorkerNeighborhoodIdCondition(query, queryParams, neighborhoodIds);
        appendWorkerNeighborhoodRoleCondition(query, queryParams, workerRole);

        if (workerStatus != null && WorkerStatus.valueOf(workerStatus.toUpperCase()) == WorkerStatus.HOT){
            appendWorkerHotCondition(query);
        }

        if (professions != null && !professions.isEmpty()) {
            appendProfessionsCondition(query, queryParams, professions);
        }
    }

    static void appendWorkerHotCondition(StringBuilder query) {
        query.append(" AND (SELECT AVG(rating) FROM reviews r WHERE r.workerid = w.userid) > 4");
    }

    static void appendInitialWhereClause(StringBuilder query) {
        query.append(" WHERE 1 = 1");
    }

    static void appendChannelCondition(StringBuilder query, List<Object> queryParams, Long channelId) {
        query.append(" AND channel.channelid = ?");
        queryParams.add(channelId);
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

    static void appendWorkerNeighborhoodIdCondition(StringBuilder query, List<Object> queryParams, long[] neighborhoodIds) {
        if (neighborhoodIds.length > 0) {
            query.append(" AND wn.neighborhoodid IN (");
            for (int i = 0; i < neighborhoodIds.length; i++) {
                query.append("?");
                queryParams.add(neighborhoodIds[i]);
                if (i < neighborhoodIds.length - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
        }
    }

    static void appendWorkerNeighborhoodRoleCondition(StringBuilder query, List<Object> queryParams, String workerRole) {
        if (workerRole != null) {
            query.append(" AND wn.role = ?");
            queryParams.add(workerRole);
        }
    }

    static void appendProfessionsCondition(StringBuilder query, List<Object> queryParams, List<String> professions) {
        query.append(" AND (");
        query.append("SELECT COUNT(*) FROM workers_professions wp JOIN professions p ON wp.professionid = p.professionid");
        query.append(" WHERE wp.workerid = w.userid AND p.profession IN (");

        appendParams(query, queryParams, professions);

        query.append(")");
        query.append(") > 0");
    }


    static void appendTagsCondition(StringBuilder query, List<Object> queryParams, List<Long> tagIds) {
        query.append(" AND EXISTS (");
        query.append("SELECT 1 FROM posts_tags pt JOIN tags t ON pt.tagid = t.tagid");
        query.append(" WHERE pt.postid = p.postid AND t.tag IN (");

        appendLongParams(query, queryParams, tagIds);

        query.append(")");
        query.append(" HAVING COUNT(DISTINCT pt.tagid) = ?)"); // Ensure all specified tags exist
        queryParams.add(tagIds.size());
    }

    static void appendParams(StringBuilder query, List<Object> queryParams, List<String> tags) {
        // Third parameter will either be tags or professions
        for (int i = 0; i < tags.size(); i++) {
            String formattedTag = capitalizeFirstLetter(tags.get(i));
            query.append("?");
            queryParams.add(formattedTag); // Use the formatted tag/profession name
            if (i < tags.size() - 1) {
                query.append(", ");
            }
        }
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

    static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
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

    static void appendDateClause(StringBuilder query) {
        query.append(" ORDER BY postdate DESC");
    }

    /*
    static void appendCommonProductConditions(StringBuilder query, List<Object> queryParams, long neighborhoodId, List<String> tags) {
        appendInitialWhereClause(query);
        appendProductNeighborhoodIdCondition(query, queryParams, neighborhoodId);

        if (tags != null && !tags.isEmpty()) {
            appendTagsCondition(query, queryParams, tags);
        }
    }
     */

    static void appendProductNeighborhoodIdCondition(StringBuilder query, List<Object> queryParams, long neighborhoodId) {
        query.append(" AND u.neighborhoodid = ?");
        queryParams.add(neighborhoodId);
    }

}
