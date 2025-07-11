package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.InsertionException;
import ar.edu.itba.paw.models.Entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;

import static ar.edu.itba.paw.persistence.TestConstants.DATE_1;
import static ar.edu.itba.paw.persistence.TestConstants.DEPARTMENT_NAME_1;

@Component
public class TestInserter {

    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public TestInserter(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public long createChannel(String channelName) {
        final Channel channel = new Channel.Builder()
                .channel(channelName)
                .isBase(false)
                .build();
        em.persist(channel);
        em.flush();
        return channel.getChannelId();
    }

    public long createChannel(String channelName, boolean isBase) {
        final Channel channel = new Channel.Builder()
                .channel(channelName)
                .isBase(isBase)
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

    public long createNeighborhood(String neighborhoodName, boolean isBase) {
        Neighborhood neighborhood = new Neighborhood.Builder()
                .name(neighborhoodName)
                .isBase(isBase)
                .build();
        em.persist(neighborhood);
        em.flush();
        return neighborhood.getNeighborhoodId();
    }

    public void createLike(long postId, long userId) {
        Like like = new Like(em.find(Post.class, postId), em.find(User.class, userId), new Date(System.currentTimeMillis()));
        em.persist(like);
        em.flush();
    }

    public long createEvent(String name, String description, Date date, long startTimeId, long endTimeId, long neighborhoodId) {
        Event event = new Event.Builder()
                .name(name)
                .description(description)
                .date(date)
                .startTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, startTimeId))
                .endTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, endTimeId))
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
                           long neighborhoodId, Language language, boolean darkMode, UserRole role, int identification, Date date) {
        User user = new User.Builder()
                .name(name)
                .mail(mail)
                .surname(surname)
                .password(password)
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .darkMode(darkMode)
                .language(language)
                .creationDate(date)
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

    public long createResource(long neighborhoodId, String title, String description, long imageId) {
        Resource resource = new Resource.Builder()
                .title(title)
                .description(description)
                .image(em.find(Image.class, imageId))
                .neighborhood(em.find(Neighborhood.class, neighborhoodId))
                .build();
        em.persist(resource);
        em.flush();
        return resource.getResourceId();
    }

    public void createChannelMapping(long neighborhoodId, long channelId) {
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

    public long createBooking(long userId, long amenityAvailabilityId, Date reservationDate) {
        Booking booking = new Booking.Builder()
                .user(em.find(User.class, userId))
                .bookingDate(reservationDate)
                .amenityAvailability(em.find(Availability.class, amenityAvailabilityId))
                .build();
        em.persist(booking);
        em.flush();
        return booking.getBookingId();
    }

    public int createDay(String dayName) {
        Day day = new Day.Builder()
                .dayName(dayName)
                .build();
        em.persist(day);
        em.flush();
        return Math.toIntExact(day.getDayId());
    }

    public long createTime(Time timeInterval) {
        ar.edu.itba.paw.models.Entities.Time time = new ar.edu.itba.paw.models.Entities.Time.Builder()
                .timeInterval(timeInterval)
                .build();
        em.persist(time);
        em.flush();
        return time.getTimeId();
    }

    public void createAffiliation(long workerId, long neighborhoodId, WorkerRole workerRole) {
        Affiliation affiliation = new Affiliation(em.find(Worker.class, workerId), em.find(Neighborhood.class, neighborhoodId), workerRole, DATE_1);
        em.persist(affiliation);
        em.flush();
    }

    public long createReview(long workerId, long userId, float rating, String reviewString, Date date) {
        Review review = new Review.Builder()
                .user(em.find(User.class, userId))
                .worker(em.find(Worker.class, workerId))
                .rating(rating)
                .review(reviewString)
                .date(date)
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
        Specialization specialization = new Specialization(em.find(Worker.class, workerId), em.find(ar.edu.itba.paw.models.Entities.Profession.class, professionId));

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
        Shift shift = new Shift.Builder()
                .day(em.find(Day.class, dayId))
                .startTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, startTimeId))
                .build();
        em.persist(shift);
        em.flush();
        return shift.getShiftId();
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

    public long createProfession(String professionType) {
        final ar.edu.itba.paw.models.Entities.Profession profession = new ar.edu.itba.paw.models.Entities.Profession.Builder()
                .profession(professionType)
                .build();
        em.persist(profession);
        em.flush();
        return profession.getProfessionId();
    }

    public long createProduct(String name, String description, Double price, boolean used,
                              long primaryPictureId, long secondaryPictureId, long tertiaryPictureId,
                              long sellerId,
                              long departmentId, long units) {
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
                .remainingUnits(units)
                .build();
        em.persist(product);
        em.flush();
        return product.getProductId();
    }

    public long createDepartment(String departmentName) {
        final Department department = new Department.Builder()
                .department(departmentName)
                .build();
        em.persist(department);
        em.flush();
        return department.getDepartmentId();
    }

    public long createInquiry(String message, String reply, long productId, long userId) {
        Inquiry inquiry = new Inquiry.Builder()
                .product(em.find(Product.class, productId))
                .user(em.find(User.class, userId))
                .message(message)
                .build();
        em.persist(inquiry);
        em.flush();
        return inquiry.getInquiryId();
    }

    public long createRequest(long productId, long userId, String message, RequestStatus requestStatus) {
        Request request = new Request.Builder()
                .product(em.find(Product.class, productId))
                .user(em.find(User.class, userId))
                .message(message)
                .status(requestStatus)
                .build();
        em.persist(request);
        em.flush();
        return request.getRequestId();
    }

    public void createTagMapping(long neighborhoodId, long tagId) {
        TagMapping tagMapping = new TagMapping(em.find(Neighborhood.class, neighborhoodId), em.find(Tag.class, tagId));
        em.persist(tagMapping);
        em.flush();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------- OVERLOADS -----------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

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
        Date date = DATE_1;
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
        String mail = "dummy@mail.com";
        String password = "password";
        String name = "Dummy";
        String surname = "User";
        int id = 43243846;
        Language lang = Language.ENGLISH;
        boolean dm = false;
        UserRole role = UserRole.NEIGHBOR;
        return createUser(mail, password, name, surname, neighborhoodId, lang, dm, role, id, DATE_1);
    }

    public long createUser(String mail, long neighborhoodId) {
        String password = "password";
        String name = "Dummy";
        String surname = "User";
        int id = 43243846;
        Language lang = Language.ENGLISH;
        boolean dm = false;
        UserRole role = UserRole.NEIGHBOR;
        return createUser(mail, password, name, surname, neighborhoodId, lang, dm, role, id, DATE_1);
    }

    public long createUser(String mail, UserRole role, long neighborhoodId) {
        String password = "password";
        String name = "Dummy";
        String surname = "User";
        int id = 43243846;
        Language lang = Language.ENGLISH;
        boolean dm = false;
        return createUser(mail, password, name, surname, neighborhoodId, lang, dm, role, id, DATE_1);
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

    public int createDay() {
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
        return createReview(workerId, userId, rating, review, DATE_1);
    }

    public long createWorker(long workerId) {
        String phoneNumber = "11-2222-3333";
        String address = "Somewhere 1232";
        String businessName = "Jo&Co";
        return createWorker(workerId, phoneNumber, address, businessName);
    }

    public long createAmenity(long neighborhoodId) {
        String name = "Amenity Name";
        String description = "Amenity Description";
        return createAmenity(name, description, neighborhoodId);
    }

    public long createImage() {
        byte[] fakeImageBytes = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        MockMultipartFile fakeImage = new MockMultipartFile("image", "fake.jpg", "image/jpeg", fakeImageBytes);
        return createImage(fakeImage);
    }

    public long createProfession() {
        return createProfession("Happy Developer");
    }

    public long createProduct(long primaryPictureId, long secondaryPictureId, long tertiaryPictureId,
                              long sellerId, long departmentId) {
        String name = "Iphone";
        String description = "Super Iphone";
        double price = 23432;
        boolean used = true;
        long units = 1L;
        return createProduct(name, description, price, used, primaryPictureId, secondaryPictureId, tertiaryPictureId, sellerId, departmentId, units);
    }

    public long createProduct(String name, long primaryPictureId, long secondaryPictureId, long tertiaryPictureId,
                              long sellerId, long departmentId, Long buyerId) {
        String description = "Super Iphone";
        double price = 23432;
        boolean used = true;
        long units = 1L;
        long pKey = createProduct(name, description, price, used, primaryPictureId, secondaryPictureId, tertiaryPictureId, sellerId, departmentId, units);
        if (buyerId != null) {
            createRequest(pKey, buyerId, "Gimme that", RequestStatus.ACCEPTED);
            em.find(Product.class, pKey).setRemainingUnits(0L);
        }
        return pKey;
    }

    public long createDepartment() {
        return createDepartment(DEPARTMENT_NAME_1);
    }

    public long createInquiry(long productId, long userId) {
        String message = "Hohohoho que caro esta todo";
        String reply = "Asi es la vida";
        return createInquiry(message, reply, productId, userId);
    }

    public long createRequest(long productId, long userId) {
        String message = "Hello";
        return createRequest(productId, userId, message, RequestStatus.REQUESTED);
    }

    public long createRequest(long productId, long userId, RequestStatus rq) {
        String message = "Hello";
        return createRequest(productId, userId, message, rq);
    }

    public void createAffiliation(long workerId, long neighborhoodId) {
        createAffiliation(workerId, neighborhoodId, WorkerRole.VERIFIED_WORKER);
    }
}