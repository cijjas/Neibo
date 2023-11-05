package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.models.JunctionEntities.*;
import ar.edu.itba.paw.models.MainEntities.*;
import ar.edu.itba.paw.models.MainEntities.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TestInserter {

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
    private final SimpleJdbcInsert channelMappingInserter;
    private final SimpleJdbcInsert productInserter;
    private final SimpleJdbcInsert departmentInserter;
    private final SimpleJdbcInsert inquiryInserter;
    private final SimpleJdbcInsert requestInserter;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    public TestInserter(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
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
        this.channelMappingInserter = new SimpleJdbcInsert(dataSource)
                .withTableName("neighborhoods_channels");
        this.productInserter = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("productid")
                .withTableName("products");
        this.requestInserter = new SimpleJdbcInsert(dataSource)
                .withTableName("products_users_requests");
        this.inquiryInserter = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("inquiryid")
                .withTableName("products_users_inquiries");
        this.departmentInserter = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("departmentid")
                .withTableName("departments");
    }

    public long createChannel(String channelName) {
        final Channel channel = new Channel.Builder()
                .channel(channelName)
                .build();
        em.persist(channel);
        em.flush();
        return channel.getChannelId();
    }

    public long createNeighborhood(String neighborhoodName) {
        Neighborhood neighborhood = new Neighborhood.Builder()
                .name(neighborhoodName)
                .build();
        em.persist(neighborhood);
        em.flush();
        return neighborhood.getNeighborhoodId();
    }

    public void createLike(long postId, long userId) {
        Like like = new Like(em.find(Post.class, postId), em.find(User.class, userId));
        em.persist(like);
        em.flush();
    }

    public long createEvent(String name, String description, Date date, long startTimeId, long endTimeId, long neighborhoodId) {
        Event event = new Event.Builder()
                .name(name)
                .description(description)
                .date(date)
                .startTime(em.find(ar.edu.itba.paw.models.MainEntities.Time.class, startTimeId))
                .endTime(em.find(ar.edu.itba.paw.models.MainEntities.Time.class, endTimeId))
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .build();
        em.persist(event);
        em.flush();
        return event.getEventId();
    }

    public long createContact(long neighborhoodId, String contactName, String contactAddress, String contactPhone) {
        Contact contact = new Contact.Builder()
                .contactAddress(contactAddress)
                .contactName(contactName)
                .contactPhone(contactPhone)
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .build();
        em.persist(contact);
        em.flush();
        return contact.getContactId();
    }

    public long createComment(String commentText, long userId, long postId) {
        Comment comment = new Comment.Builder()
                .comment(commentText)
                .user(em.find(User.class, userId))
                .post(em.find(Post.class, postId))
                .build();
        em.persist(comment);
        em.flush();
        return comment.getCommentId();
    }

    public void createCategorization(long tagId, long postId) {
        Categorization categorization = new Categorization(em.find(Post.class, postId), em.find(Tag.class, tagId));
        em.persist(categorization);
        em.flush();
    }

    public void createAttendance(long userId, long eventId) {
        Attendance attendance = new Attendance(em.find(User.class, userId), em.find(Event.class, eventId));
        em.persist(attendance);
        em.flush();
    }

    public long createPost(String title, String description, long userId, long channelId, long imageId) {
        Post post = new Post.Builder()
                .title(title)
                .description(description)
                .user(em.find(User.class, userId))
                .channel(em.find(Channel.class, channelId))
                .postPicture(em.find(Image.class, imageId))
                .build();
        em.persist(post);
        em.flush();
        return post.getPostId();
    }

    public long createUser(String mail, String password, String name, String surname,
                           long neighborhoodId, Language language, boolean darkMode, UserRole role, int identification) {
        User user = new User.Builder()
                .name(name).mail(mail)
                .surname(surname)
                .password(password)
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .darkMode(darkMode)
                .language(language)
                .role(role)
                .identification(identification)
                .build();
        em.persist(user);
        em.flush();
        return user.getUserId();
    }

    public long createTag(String name) {
        Tag tag = new Tag.Builder()
                .tag(name)
                .build();
        em.persist(tag);
        em.flush();
        return tag.getTagId();
    }

    public void createSubscription(long userId, long postId) {
        Subscription subscription = new Subscription(em.find(Post.class, postId), em.find(User.class, userId));
        em.persist(subscription);
        em.flush();
    }

    public long createResource(long neighborhoodId, String title, String description, long imageId) {
        Resource resource = new Resource.Builder()
                .title(title)
                .description(description)
                .image(em.find(Image.class,imageId))
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .build();
        em.persist(resource);
        em.flush();
        return resource.getResourceId();
    }

    public long createReservation(long amenityId, long userId, java.sql.Date date, Time startTime, Time endTime) {
        Booking booking = new Booking.Builder()
                .user(em.find(User.class, userId))
                .bookingDate(date)
                .amenityAvailability(em.find(Availability.class, amenityId))
                .build();
        em.persist(booking);
        return booking.getBookingId();
    }

    public void createChannelMapping(Number neighborhoodId, Number channelId) {
        ChannelMapping channelMapping = new ChannelMapping(em.find(Neighborhood.class, neighborhoodId), em.find(Channel.class, channelId));
        em.persist(channelMapping);
        em.flush();
    }

    public long createAvailability(long amenityId, long shiftId) {
        Availability availability = new Availability.Builder()
                .amenity(em.find(Amenity.class, amenityId))
                .shift(em.find(Shift.class, shiftId))
                .build();
        em.persist(availability);
        em.flush();
        return availability.getAmenityAvailabilityId();
    }

    public long createBooking(long userId, long amenityAvailabilityId, java.sql.Date reservationDate) {
        Booking booking = new Booking.Builder()
                .user(em.find(User.class, userId))
                .bookingDate(reservationDate)
                .amenityAvailability(em.find(Availability.class, amenityAvailabilityId))
                .build();
        em.persist(booking);
        em.flush();
        return booking.getBookingId();
    }

    public long createDay(String dayName) {
        Day day = new Day.Builder()
                .dayName(dayName)
                .build();
        em.persist(day);
        em.flush();
        return day.getDayId();
    }

    public long createTime(Time timeInterval) {
        ar.edu.itba.paw.models.MainEntities.Time time =  new ar.edu.itba.paw.models.MainEntities.Time.Builder()
                .timeInterval(timeInterval)
                .build();
        em.persist(time);
        em.flush();
        return time.getTimeId();
    }

    public void createWorkerArea(long workerId, long neighborhoodId) {
        WorkerArea workerArea = new WorkerArea(em.find(Worker.class, workerId), em.find(Neighborhood.class, neighborhoodId));
        em.persist(workerArea);
        em.flush();
    }

    public long createReview(long workerId, long userId, float rating, String reviewString) {
        Review review = new Review.Builder()
                .user(em.find(User.class, userId))
                .worker(em.find(Worker.class, workerId))
                .rating(rating)
                .review(reviewString)
                .build();
        em.persist(review);
        em.flush();
        return review.getReviewId();
    }

    public long createWorker(long workerId, String phoneNumber, String address, String businessName) {
        Worker worker = new Worker.Builder()
                .workerId(workerId)
                .user(em.find(User.class, workerId))
                .phoneNumber(phoneNumber)
                .address(address)
                .businessName(businessName)
                .build();
        em.persist(worker);
        em.flush();
        return worker.getWorkerId();
    }


    public void createSpecialization(long workerId, long professionId) {
        Specialization specialization = new Specialization(em.find(Worker.class, workerId), em.find(Profession.class, professionId));
        em.persist(specialization);
        em.flush();
    }

    public long createAmenity(String name, String description, long neighborhoodId) {
        Amenity amenity = new Amenity.Builder()
                .name(name)
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .description(description)
                .build();
        em.persist(amenity);
        em.flush();
        return amenity.getAmenityId();
    }

    public long createShift(long dayId, long startTimeId) {
        Map<String, Object> data = new HashMap<>();
        data.put("dayid", dayId);
        data.put("starttime", startTimeId);

        return shiftInsert.executeAndReturnKey(data).longValue();
    }

    public long createImage(MultipartFile image) {
        byte[] imageBytes;
        try {
            imageBytes = image.getBytes();
        } catch (IOException e) {
            throw new InsertionException("An error occurred whilst storing the image");
        }
        Image img = new Image.Builder()
                .image(imageBytes)
                .build();
        em.persist(img);
        em.flush();
        return img.getImageId();
    }

    public long createProfession(Professions professionType) {
        final Profession profession = new Profession.Builder()
                .profession(professionType)
                .build();
        em.persist(profession);
        em.flush();
        return profession.getProfessionId();
    }

    public long createProduct(String name, String description, Double price, boolean used,
                              long primaryPictureId, long secondaryPictureId, long tertiaryPictureId,
                              long sellerId, Long buyerId, long departmentId){
        Product product = new Product.Builder()
                .name(name)
                .description(description)
                .price(price)
                .used(used)
                .department(em.find(Department.class, departmentId))
                .seller(em.find(User.class, sellerId))
                .primaryPicture(em.find(Image.class, primaryPictureId))
                .secondaryPicture(em.find(Image.class, secondaryPictureId))
                .tertiaryPicture(em.find(Image.class, tertiaryPictureId))
                .build();
        if (buyerId != null )
            product.setBuyer(em.find(User.class, buyerId));
        em.persist(product);
        em.flush();
        return product.getProductId();
    }

    public long createDepartment(ar.edu.itba.paw.enums.Department departmentType){
        final Department department = new Department.Builder()
                .department(departmentType)
                .build();
        em.persist(department);
        em.flush();
        return department.getDepartmentId();
    }

    public long createInquiry(String message, String reply, long productId, long userId){
        Inquiry inquiry = new Inquiry.Builder()
                .product(em.find(Product.class, productId))
                .user(em.find(User.class, userId))
                .message(message)
                .build();
        em.persist(inquiry);
        em.flush();
        return inquiry.getInquiryId();
    }

    public void createRequest(long productId, long userId){
        Request request = new Request.Builder()
                .product(em.find(Product.class, productId))
                .user(em.find(User.class, userId))
                .build();
        em.persist(request);
        em.flush();
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


    public long createEvent(long neighborhoodId, long startTime, long endTime) {
        String name = "Dummy Event Name";
        String description = "Me estoy volviendo loco";
        Date date = java.sql.Date.valueOf("2001-3-14");
        return createEvent(name, description, date, startTime, endTime, neighborhoodId);
    }

    public long createEvent(long neighborhoodId, long startTime, long endTime, Date date) {
        String name = "Dummy Event Name";
        String description = "Me estoy volviendo loco";
        return createEvent(name, description, date, startTime, endTime, neighborhoodId);
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
        java.sql.Date date = java.sql.Date.valueOf("2022-12-12");
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

    public long createImage() {
        // Create a small byte array for a fake image (e.g., a 1x1 white pixel)
        byte[] fakeImageBytes = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

        // Create a MockMultipartFile using the fake image bytes
        MockMultipartFile fakeImage = new MockMultipartFile("image", "fake.jpg", "image/jpeg", fakeImageBytes);
        return createImage(fakeImage);
    }

    public long createProfession() {
        return createProfession(Professions.PLUMBER);
    }

    public long createProduct(long primaryPictureId, long secondaryPictureId, long tertiaryPictureId,
                              long sellerId, Long buyerId, long departmentId){
        String name = "Iphone";
        String description = "Super Iphone";
        double price = 23432;
        boolean used = true;

        return createProduct(name, description, price, used, primaryPictureId, secondaryPictureId, tertiaryPictureId, sellerId, buyerId, departmentId);
    }

    public long createProduct(String name, long primaryPictureId, long secondaryPictureId, long tertiaryPictureId,
                              long sellerId, Long buyerId, long departmentId){
        String description = "Super Iphone";
        double price = 23432;
        boolean used = true;

        return createProduct(name, description, price, used, primaryPictureId, secondaryPictureId, tertiaryPictureId, sellerId, buyerId, departmentId);
    }

    public long createDepartment(){
        return createDepartment(ar.edu.itba.paw.enums.Department.ARTS_CRAFTS);
    }

    public long createInquiry(long productId, long userId){
        String message = "Hohohoho que caro esta todo";
        String reply = "Asi es la vida";
        return createInquiry(message, reply, productId, userId);
    }
}