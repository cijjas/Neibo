export interface Links {
    self: string;
    amenity: string;
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
    attendees: string;
    product: string;
    products: string;
    user: string;
    users: string;
    contacts: string;
    channel: string;
    channels: string;
    event: string;
    events: string;
    resources: string;
    postPicture: string;
    comments: string;
    tags: string;
    likes: string;
    primaryPicture: string;
    secondaryPicture: string;
    tertiaryPicture: string;
    seller: string;
    department: string;
    inquiries: string;
    requests: string;
    image: string;
    profilePicture: string;
    bookings: string;
    likedPosts: string;
    purchases: string;
    sales: string;
    backgroundPicture: string;
    reviews: string;
    professions: string;
    workerNeighborhoods: string;
    neighborhoods: string;
    language: string;
    likeCount: string;
}

export interface AffiliationDto {
    workerRole: String;
    _links: Links;
}

export interface AmenityDto {
    name: string;
    description: string;
    _links: Links;
}

export interface AttendanceDto {
    _links: Links;
}

export interface BookingDto {
    date: Date;
    _links: Links;
}

export interface ChannelDto {
    name: string;
    _links: Links;
}

export interface CommentDto {
    comment: string;
    date: Date;
    _links: Links;
}

export interface ContactDto {
    name: string;
    address: string;
    phone: string;
    _links: Links;
}

export interface DepartmentDto {
    name: string;
    _links: Links;
}

export interface EventDto {
    name: string;
    description: string;
    date: Date;
    startTime: string;
    endTime: string;
    _links: Links;
}

export interface ImageDto {
    image: Uint8Array;
    _links: Links;
}

export interface InquiryDto {
    message: string;
    reply: string;
    date: Date;
    _links: Links;
}

export interface LanguageDto {
    language: string
    _links: Links
}

export interface LikeDto {
    date: Date;
    _links: Links;
}

export interface NeighborhoodDto {
    name: string;
    _links: Links;
}

export interface PostDto {
    title: string;
    description: string;
    date: Date;
    _links: Links;
}

export interface PostStatusDto {
    postStatus: string
    _links: Links
}

export interface ProductDto {
    name: string;
    description: string;
    price: number;
    used: boolean;
    remainingUnits: number;
    date: Date;
    firstImage: Uint8Array;
    secondImage: Uint8Array;
    thirdImage: Uint8Array;
    _links: Links;
}

export interface ProductStatusDto {
    productStatus: string
    _links: Links
}

export interface ProfessionDto {
    profession: string;
    _links: Links;
}

export interface RequestDto {
    message: string;
    unitsRequested: number;
    requestDate: Date;
    purchaseDate: Date;
    _links: Links;
}

export interface RequestStatusDto {
    requestStatus: string
    _links: Links
}

export interface ResourceDto {
    title: string;
    description: string;
    _links: Links;
}

export interface ReviewDto {
    rating: number;
    review: string;
    date: Date;
    _links: Links;
}

export interface ShiftDto {
    startTime: string;
    day: string;
    _links: Links;
}

export interface TagDto {
    name: string;
    _links: Links;
}

export interface TransactionTypeDto {
    transactionType: string
    _links: Links
}

export interface UserDto {
    mail: string;
    name: string;
    surname: string;
    darkMode: boolean;
    phoneNumber: string;
    identification: number;
    creationDate: Date;
    _links: Links;
}

export interface UserRoleDto {
    userRole: string
    _links: Links
}

export interface WorkerDto {
    phoneNumber: string;
    businessName: string;
    address: string;
    bio: string;
    averageRating: number;
    _links: Links;
}

export interface WorkerRoleDto {
    workerRole: string
    _links: Links
}

export interface UserRoleDto {
    userRole: string
    _links: Links
}
