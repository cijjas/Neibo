export interface RequestDto {
    requestId: number;
    message: string;
    requestDate: Date;
    fulfilled: boolean;
    product: string;
    user: string;
    self: string;
}
