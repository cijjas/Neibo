package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import enums.Language;
import enums.Table;
import enums.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
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
    private final SimpleJdbcInsert availabilityInsert;
    private final SimpleJdbcInsert bookingInsert;
    private final SimpleJdbcInsert dayInsert;
    private final SimpleJdbcInsert timeInsert;
    private final SimpleJdbcInsert neighborhoodWorkerInsert;
    private final SimpleJdbcInsert reviewInsert;
    private final SimpleJdbcInsert workerInsert;
    private final SimpleJdbcInsert professionWorkerInsert;
    private final SimpleJdbcInsert amenityInsert;
    private final SimpleJdbcInsert shiftInsert;
    private final SimpleJdbcInsert imageInsert;
    private final SimpleJdbcInsert professionInsertion;

    // private final SimpleJdbcInsert professionInsert; no insertion in this dao

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
        this.availabilityInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("amenities_shifts_availability")
                .usingGeneratedKeyColumns("amenityavailabilityid");
        this.bookingInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("users_availability")
                .usingGeneratedKeyColumns("bookingid");
        this.dayInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("days")
                .usingGeneratedKeyColumns("dayid");
        this.timeInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("times")
                .usingGeneratedKeyColumns("timeid");
        this.neighborhoodWorkerInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("workers_neighborhoods");
        this.professionWorkerInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("workers_professions");
        this.reviewInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("reviewid")
                .withTableName("reviews");
        this.workerInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("workers_info");
        this.amenityInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("amenityid")
                .withTableName("amenities");
        this.shiftInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("shifts")
                .usingGeneratedKeyColumns("shiftid");
        this.imageInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("imageid")
                .withTableName("images");
        this.professionInsertion = new SimpleJdbcInsert(dataSource)
                .withTableName("professions")
                .usingGeneratedKeyColumns("professionid");
    }

    public long createChannel(String channelName) {
        Map<String, Object> channelData = new HashMap<>();
        channelData.put("channel", channelName);
        return channelInsert.executeAndReturnKey(channelData).longValue();
    }

    public long createNeighborhood(String neighborhoodName) {
        Map<String, Object> neighborhoodData = new HashMap<>();
        neighborhoodData.put("neighborhoodname", neighborhoodName);
        return neighborhoodInsert.executeAndReturnKey(neighborhoodData).longValue();
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

    public long createEvent(String name, String description, Date date, long duration, long neighborhoodId) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("date", date);
        data.put("duration", duration);
        data.put("neighborhoodid", neighborhoodId);
        return eventInsert.executeAndReturnKey(data).longValue();
    }

    public long createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone) {
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodid", neighborhoodId);
        data.put("contactname", contactName);
        data.put("contactaddress", contactAddress);
        data.put("contactphone", contactPhone);
        return contactInsert.executeAndReturnKey(data).longValue();
    }

    public long createComment(String comment, long userId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("comment", comment);
        data.put("commentdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userId);
        data.put("postid", postId);
        return commentInsert.executeAndReturnKey(data).longValue();
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

    public long createPost(String title, String description, long userId, long channelId, long imageId) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("postdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userId);
        data.put("postPictureId", imageId == 0 ? null : imageId);
        data.put("channelid", channelId);
        return postInsert.executeAndReturnKey(data).longValue();
    }

    public long createUser(String mail, String password, String name, String surname,
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
        return userInsert.executeAndReturnKey(data).longValue();
    }

    public long createTag(String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("tag", name);
        return tagInsert.executeAndReturnKey(data).longValue();
    }

    public void createSubscription(long userId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("postid", postId);
        subscriptionInsert.execute(data);
    }

    public long createResource(long neighborhoodId, String title, String description, long imageId) {
        Map<String, Object> data = new HashMap<>();
        data.put("neighborhoodid", neighborhoodId);
        data.put("resourcetitle", title);
        data.put("resourcedescription", description);
        data.put("resourceimageid", imageId == 0 ? null : imageId);
        return resourceInsert.executeAndReturnKey(data).longValue();
    }

    public long createReservation(long amenityId, long userId, Date date, Time startTime, Time endTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("amenityid", amenityId);
        data.put("userid", userId);
        data.put("date", date);
        data.put("starttime", startTime);
        data.put("endtime", endTime);
        return reservationInsert.executeAndReturnKey(data).longValue();
    }

    public long createAvailability(long amenityId, long shiftId) {
        Map<String, Object> data = new HashMap<>();
        data.put("amenityid", amenityId);
        data.put("shiftid", shiftId);
        return availabilityInsert.executeAndReturnKey(data).longValue();
    }

    public long createBooking(long userId, long amenityAvailabilityId, java.sql.Date reservationDate) {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userId);
        data.put("amenityavailabilityid", amenityAvailabilityId);
        data.put("date", reservationDate);
        return bookingInsert.executeAndReturnKey(data).longValue();
    }

    public long createDay(String day) {
        Map<String, Object> data = new HashMap<>();
        data.put("dayname", day);

        return dayInsert.executeAndReturnKey(data).longValue();
    }

    public long createTime(Time timeInterval) {
        Map<String, Object> data = new HashMap<>();
        data.put("timeinterval", timeInterval);

        return timeInsert.executeAndReturnKey(data).longValue();
    }

    public void addWorkerToNeighborhood(long workerId, long neighborhoodId) {
        Map<String, Object> data = new HashMap<>();
        data.put("workerid", workerId);
        data.put("neighborhoodid", neighborhoodId);

        neighborhoodWorkerInsert.execute(data);
    }

    public long createReview(long workerId, long userId, float rating, String review) {
        Map<String, Object> data = new HashMap<>();
        data.put("workerid", workerId);
        data.put("userid", userId);
        data.put("rating", rating);
        data.put("review", review);
        data.put("date", Timestamp.valueOf(LocalDateTime.now()));

        return reviewInsert.executeAndReturnKey(data).longValue();
    }

    public void createWorker(long workerId, String phoneNumber, String address, String businessName) {
        Map<String, Object> data = new HashMap<>();
        data.put("workerId", workerId);
        data.put("phoneNumber", phoneNumber);
        data.put("address", address);
        data.put("businessName", businessName);

        workerInsert.execute(data);
    }


    public void createWorkerProfession(long workerId, long professionId) {
        Map<String, Object> data = new HashMap<>();
        data.put("workerid", workerId);
        data.put("professionid", professionId);
        professionWorkerInsert.execute(data);
    }

    public long createAmenity(String name, String description, long neighborhoodId) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("neighborhoodid", neighborhoodId);

        return amenityInsert.executeAndReturnKey(data).longValue();
    }

    public long createShift(long dayId, long startTimeId) {
        Map<String, Object> data = new HashMap<>();
        data.put("dayid", dayId);
        data.put("starttime", startTimeId);

        return shiftInsert.executeAndReturnKey(data).longValue();
    }

    public long createImage(MultipartFile image) {
        Map<String, Object> data = new HashMap<>();
        byte[] imageBytes;
        try {
            imageBytes = image.getBytes();
        } catch (IOException e) {
            throw new InsertionException("An error occurred whilst setting up the test");
        }

        data.put("image", imageBytes);
        return imageInsert.executeAndReturnKey(data).longValue();
    }

    public long createProfession(String profession) {
        Map<String, Object> data = new HashMap<>();
        data.put("profession", profession);
        return professionInsertion.executeAndReturnKey(data).longValue();
    }

    // ----------------------------------------------------------------------------------------------------
    // OVERLOADS FOR SIMPLIFYING TESTING ------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------

    public long createContact(long neighborhoodId) {
        String contactName = "Dummy Contact";
        String address = "Dummy address";
        String number = "14213242";
        return createContact(neighborhoodId, contactName, address, number);
    }

    public long createChannel() {
        String channelName = "Dummy Channel";
        return createChannel(channelName);
    }

    public long createComment(long userId, long postId) {
        String comment = "Dummy Commentttt";
        return createComment(comment, userId, postId);
    }


    public long createEvent(long neighborhoodId) {
        String name = "Dummy Event Name";
        String description = "Me estoy volviendo loco";
        Date date = java.sql.Date.valueOf("2001-3-14");
        long duration = 90;
        return createEvent(name, description, date, duration, neighborhoodId);
    }

    public long createNeighborhood() {
        String name = "Dummy Neighborhood";
        return createNeighborhood(name);
    }

    public long createPost(long userId, long channelId, long imageId) {
        String title = "Dummy Title";
        String description = "Dummy Description";
        return createPost(title, description, userId, channelId, imageId);
    }

    public long createUser(long neighborhoodId) {
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

    public long createUser(String mail, long neighborhoodId) {
        // Generate dummy values
        String password = "password";
        String name = "Dummy";
        String surname = "User";
        int id = 43243846;
        Language lang = Language.ENGLISH;
        boolean dm = false;
        UserRole role = UserRole.NEIGHBOR;
        return createUser(mail, password, name, surname, neighborhoodId, lang, dm, role, id);
    }

    public long createUser(String mail, UserRole role, long neighborhoodId) {
        // Generate dummy values
        String password = "password";
        String name = "Dummy";
        String surname = "User";
        int id = 43243846;
        Language lang = Language.ENGLISH;
        boolean dm = false;
        return createUser(mail, password, name, surname, neighborhoodId, lang, dm, role, id);
    }

    public long createTag() {
        String tagName = "Dummy Tag";
        return createTag(tagName);
    }

    public long createResource(long neighborhoodId, long imageId) {
        String title = "Dummy Resource";
        String description = "Dummy Resource Description";
        return createResource(neighborhoodId, title, description, imageId);
    }

    public long createReservation(long amenityId, long userId) {
        Date date = new Date();
        Time startTime = Time.valueOf(LocalDateTime.now().toLocalTime());
        Time endTime = Time.valueOf(LocalDateTime.now().plusHours(1).toLocalTime());
        return createReservation(amenityId, userId, date, startTime, endTime);
    }

    public long createBooking(long userId, long amenityAvailabilityId) {
        java.sql.Date date = java.sql.Date.valueOf("2022-12-12");
        return createBooking(userId, amenityAvailabilityId, date);
    }

    public long createDay() {
        String dayName = "Groundhog Day";
        return createDay(dayName);
    }

    public long createTime() {
        java.sql.Time time = new java.sql.Time(System.currentTimeMillis());
        return createTime(time);
    }

    public long createReview(long workerId, long userId) {
        float rating = 2.34234F;
        String review = "Really Great Job";
        return createReview(workerId, userId, rating, review);
    }

    public void createWorker(long workerId) {
        String phoneNumber = "11-2222-3333";
        String address = "Somewhere 1232";
        String businessName = "Jo&Co";
        createWorker(workerId, phoneNumber, address, businessName);
    }

    public long createAmenity(long neighborhoodId) {
        String name = "Amenity Name";
        String description = "Amenity Description";
        return createAmenity(name, description, neighborhoodId);
    }

    public long createImage(){
        // Create a small byte array for a fake image (e.g., a 1x1 white pixel)
        byte[] fakeImageBytes = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

        // Create a MockMultipartFile using the fake image bytes
        MockMultipartFile fakeImage = new MockMultipartFile("image", "fake.jpg", "image/jpeg", fakeImageBytes);
        return createImage(fakeImage);
    }

    public long createProfession() {
        String profession = "Some Profession";
        return createProfession(profession);
    }
}