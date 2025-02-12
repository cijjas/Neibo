// @index('./**/*.ts', f => `export * from '${f.path}'`)
export * from './core.module';
export * from './guards/role.guard';
export * from './guards/auth.guard';
export * from './interceptors/error.interceptor';
export * from './interceptors/jwt.interceptor';
export * from './services/auth.service';
export * from '@shared/services/utils/image.service';
export * from './services/link.service';
export * from './services/preferences.service';
export * from '@shared/services/utils/toast.service';
export * from '@shared/services/utils/confirmation.service';
export * from './services/user-session.service';
export * from './services/app-init.service';
