export interface ProductDto {
    productId: number;
    name: string;
    description: string;
    price: number;
    used: boolean;
    remainingUnits: number;
    primaryPicture: string;
    secondaryPicture: string;
    tertiaryPicture: string;
    seller: string;
    department: string;
    inquiries: string;
    requests: string;
    self: string;
}
