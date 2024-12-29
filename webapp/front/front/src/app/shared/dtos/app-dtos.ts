export interface Links {
    [key: string]: string;
    amenity: string;
    self: string;
    worker: string;
    workers: string;
    workerRole: string;
    userRole: string;
    requestStatus: string;
    neighborhood: string;
    shift: string;
    shifts: string;
    post: string;
    posts: string;
    attendanceUsers: string;
    product: string;
    products: string;
    users: string;
    contacts: string;
    channel: string;
    channels: string;
    event: string;
    events: string;
    resources: string;
    comments: string;
    tags: string;
    likes: string;
    firstProductImage: string;
    secondProductImage: string;
    thirdProductImage: string;
    productUser: string;
    department: string;
    inquiries: string;
    requests: string;
    image: string;
    profilePicture: string;
    language: string;
    bookings: string;
    likedPosts: string;
    purchases: string;
    sales: string;
    backgroundImage: string;
    reviews: string;
    professions: string;
    workerNeighborhoods: string;
    neighborhoods: string;
    likeCount: string;
    replyUser: string;
    inquiryUser: string;
    user: string;
    attendanceUser: string;
    bookingUser: string;
    commentUser: string;
    likeUser: string;
    postUser: string;
    postImage: string;
    userImage: string;
    resourceImage: string;
    reviewUser: string;
    requestUser: string;
    attendanceCount: string;
    reviewsAverage: string;
    reviewsCount: string;
    postsCount: string;
    pendingRequestsCount: string;
}

export interface AffiliationDto {
    _links: Links;
}

export interface AmenityDto {
    name: string;
    description: string;
    selectedShifts?: string[];
    _links?: Links;
}

export interface AttendanceDto {
    _links: Links;
}

export interface AttendanceCountDto {
    count: number,
    _links: Links;
}

export interface BookingDto {
    bookingDate: Date;
    _links: Links;
}

export interface ChannelDto {
    name: string;
    _links: Links;
}

export interface CommentDto {
    message: string;
    creationDate: Date;
    _links: Links;
}

export interface ContactDto {
    name: string;
    address: string;
    phone: string;
    _links?: Links;
}

export interface DepartmentDto {
    name: string;
    _links: Links;
}

export interface EventDto {
    name: string;
    description: string;
    eventDate: Date;
    startTime: string;
    endTime: string;
    _links?: Links;
}

export interface ImageDto {
    data: Uint8Array;
    _links: Links;
}

export interface InquiryDto {
    message: string;
    reply: string;
    inquiryDate: Date;
    _links: Links;
}

export interface LanguageDto {
    name: string
    _links: Links
}

export interface LikeDto {
    user?: string;
    post?: string;
    likeDate?: Date;
    _links?: Links;
}

export interface LikeCountDto {
    count: number
    _links: Links
}

export interface NeighborhoodDto {
    name: string;
    _links: Links;
}

export interface PostDto {
    title?: string;
    body?: string;
    // solo la api lo devuelve
    creationDate?: Date;
    _links?: Links;
    // solo lo envia en usuario cuando crea
    tags?: string[];
    imageFile?: File;
    image?: string;
    channel?: string;
    user?: string;

}

export interface PostsCountDto {
    count: number,
    _links: Links;
}

export interface PostStatusDto {
    status: string
    _links: Links
}

export interface ProductDto {
    name: string;
    description: string;
    price: number;
    used: boolean;
    remainingUnits: number;
    creationDate: Date;
    firstProductImage: Uint8Array;
    secondProductImage: Uint8Array;
    thirdProductImage: Uint8Array;
    _links: Links;
}

export interface ProductStatusDto {
    status: string
    _links: Links
}

export interface ProfessionDto {
    name: string;
    _links: Links;
}

export interface RequestDto {
    message: string;
    unitsRequested: number;
    requestDate: Date;
    purchaseDate: Date;
    _links: Links;
}

export interface RequestsCountDto {
    count: number,
    _links: Links;
}

export interface RequestStatusDto {
    status: string
    _links: Links
}

export interface ResourceDto {
    title: string;
    description: string;
    image?: string;
    _links?: Links;
}

export interface ReviewDto {
    rating?: number;
    message?: string;
    creationDate?: Date;
    _links?: Links;
}

export interface ReviewsCountDto {
    count: number,
    _links: Links;
}

export interface ReviewsAverageDto {
    average: number;
    _links: Links;
}

export interface ShiftDto {
    startTime: string;
    day: string;
    isBooked: boolean;
    _links: Links;
}

export interface TagDto {
    name: string;
    _links: Links;
}

export interface TransactionTypeDto {
    type: string
    _links: Links
}

export interface UserDto {
    mail: string;
    name: string;
    surname: string;
    darkMode: boolean;
    phoneNumber: string;
    language: string;
    identification: number;
    creationDate: Date;
    _links: Links;
}

export interface UserRoleDto {
    role: string
    _links: Links
}

export interface WorkerDto {
    phoneNumber: string;
    businessName: string;
    address: string;
    bio: string;
    _links: Links;
}

export interface WorkerRoleDto {
    role: string
    _links: Links
}

export interface UserRoleDto {
    role: string
    _links: Links
}
