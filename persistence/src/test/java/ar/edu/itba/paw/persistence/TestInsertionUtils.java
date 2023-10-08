package ar.edu.itba.paw.persistence;

import enums.Language;
import enums.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestInsertionUtils {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert channelInsert;
    private final SimpleJdbcInsert neighborhoodInsert;
    private final SimpleJdbcInsert channelNeighborhoodMappingInsert;
    private final SimpleJdbcInsert likeInsert;
    private final SimpleJdbcInsert eventInsert;
    private final SimpleJdbcInsert contactInsert;
    private final SimpleJdbcInsert commentInsert;
    private final SimpleJdbcInsert categorizationInsert;
    private final SimpleJdbcInsert attendanceInsert;
    private final SimpleJdbcInsert postInsert;
    private final SimpleJdbcInsert userInsert;
    private final SimpleJdbcInsert tagInsert;
    private final SimpleJdbcInsert subscriptionInsert;
    private final SimpleJdbcInsert resourceInsert;
    private final SimpleJdbcInsert reservationInsert;

    public TestInsertionUtils(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.channelInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.channels.name())
                .usingGeneratedKeyColumns("channelid");
        this.neighborhoodInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.neighborhoods.name())
                .usingGeneratedKeyColumns("neighborhoodid");
        this.channelNeighborhoodMappingInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.neighborhoods_channels.name());
        this.likeInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.posts_users_likes.name());
        this.eventInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.events.name())
                .usingGeneratedKeyColumns("eventid");
        this.contactInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.contacts.name())
                .usingGeneratedKeyColumns("contactid");
        this.commentInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.comments.name())
                .usingGeneratedKeyColumns("commentid");
        this.categorizationInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.posts_tags.name());
        this.attendanceInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.events_users.name());
        this.postInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.posts.name())
                .usingGeneratedKeyColumns("postid");
        this.userInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.users.name())
                .usingGeneratedKeyColumns("userid");
        this.tagInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.tags.name())
                .usingGeneratedKeyColumns("tagid");
        this.subscriptionInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.posts_users_subscriptions.name());
        this.resourceInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.resources.name())
                .usingGeneratedKeyColumns("resourceid");
        this.reservationInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(Table.reservations.name())
                .usingGeneratedKeyColumns("reservationid");
    }

    public Number createChannel(String channelName) {
        Map<String, Object> channelData = new HashMap<>();
        channelData.put("channel", channelName);
        return channelInsert.executeAndReturnKey(channelData);
    }

    public Number createNeighborhood(String neighborhoodName) {
        Map<String, Object> neighborhoodData = new HashMap<>();
        neighborhoodData.put("neighborhoodname", neighborhoodName);
        return neighborhoodInsert.executeAndReturnKey(neighborhoodData);
    }

    public void createNeighborhoodChannelMapping(Number neighborhoodId, Number channelId) {
        Map<String, Object> ncData = new HashMap<>();
        ncData.put("neighborhoodid", neighborhoodId);
        ncData.put("channelid", channelId);
        channelNeighborhoodMappingInsert.execute(ncData);
    }
    public void createLike(long postId, long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("postid", postId);
        data.put("likedate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userId);
        likeInsert.execute(data);
    }

    public Number createEvent(String name, String description, Date date, long duration, long neighborhoodId) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("date", date);
        data.put("duration", duration);
        data.put("neighborhoodid", neighborhoodId);
        return eventInsert.executeAndReturnKey(data);
    }

    public Number createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone) {
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodid", neighborhoodId);
        data.put("contactname", contactName);
        data.put("contactaddress", contactAddress);
        data.put("contactphone", contactPhone);
        return contactInsert.executeAndReturnKey(data);
    }

    public Number createComment(String comment, long userId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("comment", comment);
        data.put("commentdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userId);
        data.put("postid", postId);
        return commentInsert.executeAndReturnKey(data);
    }

    public void createCategorization(long tagId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("tagid", tagId);
        data.put("postid", postId);
        categorizationInsert.execute(data);
    }

    public void createAttendance(long userId, long eventId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("eventid", eventId);
        attendanceInsert.execute(data);
    }

    public Number createPost(String title, String description, long userId, long channelId, long imageId) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("postdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userId);
        data.put("postPictureId", imageId == 0 ? null : imageId);
        data.put("channelid", channelId);
        return postInsert.executeAndReturnKey(data);
    }

    public Number createUser(String mail, String password, String name, String surname,
                           long neighborhoodId, Language language, boolean darkMode, UserRole role, int identification) {
        Map<String, Object> data = new HashMap<>();
        data.put("mail", mail);
        data.put("password", password);
        data.put("name", name);
        data.put("creationDate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("surname", surname);
        data.put("neighborhoodid", neighborhoodId);
        data.put("darkmode", darkMode);
        data.put("language", language != null ? language.toString() : null);
        data.put("role", role != null ? role.toString() : null);
        data.put("identification", identification);
        return userInsert.executeAndReturnKey(data);
    }

    public Number createTag(String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("tag", name);
        return tagInsert.executeAndReturnKey(data);
    }

    public void createSubscription(long userId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("postid", postId);
        subscriptionInsert.execute(data);
    }

    public Number createResource(long neighborhoodId, String title, String description, long imageId) {
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodid", neighborhoodId);
        data.put("resourcetitle", title);
        data.put("resourcedescription", description);
        data.put("resourceimageid", imageId == 0 ? null : imageId);
        return resourceInsert.executeAndReturnKey(data);
    }

    public Number createReservation(long amenityId, long userId, Date date, Time startTime, Time endTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("amenityid", amenityId);
        data.put("userid", userId);
        data.put("date", date);
        data.put("starttime", startTime);
        data.put("endtime", endTime);
        return reservationInsert.executeAndReturnKey(data);
    }

    // ----------------------------------------------------------------------------------------------------
    // OVERLOADS FOR SIMPLIFYING TESTING ------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------

    public Number createContact(long neighborhoodId) {
        String contactName = "Dummy Contact";
        String address = "Dummy address";
        String number = "14213242";
        return createContact(neighborhoodId, contactName, address, number);
    }

    public Number createChannel() {
        String channelName = "Dummy Channel";
        return createChannel(channelName);
    }

    public Number createComment(long userId, long postId) {
        String comment = "Dummy Commentttt";
        return createComment(comment, userId, postId);
    }


    public Number createEvent(long neighborhoodId) {
        String name = "Dummy Event Name";
        String description = "Me estoy volviendo loco";
        Date date = java.sql.Date.valueOf("2001-3-14");
        long duration = 90;
        return createEvent(name, description, date, duration, neighborhoodId) ;
    }

    public Number createNeighborhood(){
        String name = "Dummy Neighborhood";
        return createNeighborhood(name);
    }

    public Number createPost(long userId, long channelId, long imageId) {
        String title = "Dummy Title";
        String description = "Dummy Description";
        return createPost(title, description, userId, channelId, imageId);
    }

    public Number createUser(long neighborhoodId) {
        // Generate dummy values
        String mail = "dummy@mail.com";
        String password = "password";
        String name = "Dummy";
        String surname = "User";
        int id = 43243846;
        Language lang = Language.ENGLISH;
        boolean dm = false;
        UserRole role = UserRole.NEIGHBOR;
        return createUser(mail, password, name, surname, neighborhoodId, lang, dm, role, id);
    }

    public Number createTag() {
        String tagName = "Dummy Tag";
        return createTag(tagName);
    }

    public Number createResource(long neighborhoodId) {
        String title = "Dummy Resource";
        String description = "Dummy Resource Description";
        long imageId = 0;
        return createResource(neighborhoodId, title, description, imageId);
    }

    public Number createReservation(long amenityId, long userId) {
        Date date = new Date();
        Time startTime = Time.valueOf(LocalDateTime.now().toLocalTime());
        Time endTime = Time.valueOf(LocalDateTime.now().plusHours(1).toLocalTime());
        return createReservation(amenityId, userId, date, startTime, endTime);
    }
}
