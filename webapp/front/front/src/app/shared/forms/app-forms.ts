// app-forms.ts

export interface AffiliationForm {
    workerRole: string;
    worker: string;
    neighborhood: string;
}

export interface AmenityForm {
    name: string;
    description: string;
    selectedShifts: string[];
}

export interface AttendanceForm {
    user: string;
}

export interface BookingForm {
    amenity: string;
    shift: string;
    bookingDate: string; // Use ISO string format for dates YYYY-MM-DD
    user: string;
}

export interface ChannelForm {
    name: string;
}

export interface CommentForm {
    message: string;
    user: string;
}

export interface ContactForm {
    name: string;
    address: string;
    phone: string;
}

export interface DepartmentForm {
    name: string;
}

export interface EventForm {
    name: string;
    description: string;
    eventDate: string; // Use ISO string format for dates
    startTime: string;
    endTime: string;
}

export interface ImageForm {
    data: File; // Use File type for image uploads
}

export interface InquiryForm {
    message: string;
    user: string;
}

export interface LanguageForm {
    name: string;
}

export interface LikeForm {
    post: string;
    user: string;
}

export interface LikeCountForm {
    postId?: string;
    userId?: string;
}

export interface NeighborhoodForm {
    name: string;
}

export interface ProductForm {
    name: string;
    description: string;
    price: number;
    used: boolean;
    remainingUnits: number;
    department: string;
    images?: File[]; // Optional multiple image uploads
    user: string;
}

export interface RequestForm {
    message: string;
    product: string;
    unitsRequested: number;
    user: string;
    requestStatus?: string;
}

export interface ResourceForm {
    title: string;
    description: string;
    image?: File; // Optional image upload
}

export interface ReviewForm {
    rating: number;
    message: string;
    user: string;
}

export interface ShiftForm {
    startTime: string;
    day: string;
    isBooked?: boolean;
}

export interface TagForm {
    name: string;
}

export interface TransactionTypeForm {
    type: string;
}

export interface UserForm {
    mail: string;
    name: string;
    surname: string;
    password: string;
    identification: number;
    language: string;
    userRole: string;
    phoneNumber: string;
    profilePicture?: File; // Optional image upload
    darkMode: boolean;
}

export interface WorkerForm {
    user: string;
    professions: string[];
    phoneNumber: string;
    businessName: string;
    address: string;
    bio?: string;
    backgroundPicture?: File; // Optional image upload
}
