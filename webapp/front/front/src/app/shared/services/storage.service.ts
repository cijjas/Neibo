import {Injectable} from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class StorageService {


    setStorageItem(key: string, value: string) {
        this.storageType().setItem(key, value);
    }

    getStorageItem(key: string) {
        const item = this.storageType().getItem(key);
        return item ? JSON.parse(item) : null;
    }
    storageType() :Storage{
        return localStorage.getItem('rememberMe') ? localStorage : sessionStorage;
    }

}
