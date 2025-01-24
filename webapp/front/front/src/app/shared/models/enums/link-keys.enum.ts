export enum LinkKey {
  // -----------------------------------------------------------------
  // Root links
  // -----------------------------------------------------------------
  ACCEPTED_REQUEST_STATUS = 'root:acceptedRequestStatus',

  // ROLES
  SUPER_ADMINISTRATOR_USER_ROLE = 'root:superAdministratorUserRole',
  ADMINISTRATOR_USER_ROLE = 'root:administratorUserRole',
  NEIGHBOR_USER_ROLE = 'root:neighborUserRole',
  REJECTED_WORKER_ROLE = 'root:rejectedWorkerRole',
  UNVERIFIED_NEIGHBOR_USER_ROLE = 'root:unverifiedNeighborUserRole',
  REJECTED_USER_ROLE = 'root:rejectedUserRole',
  UNVERIFIED_WORKER_ROLE = 'root:unverifiedWorkerRole',
  WORKER_USER_ROLE = 'root:workerUserRole',
  VERIFIED_WORKER_ROLE = 'root:verifiedWorkerRole',

  AFFILIATIONS = 'root:affiliations',
  BOUGHT_PRODUCT_STATUS = 'root:boughtProductStatus',
  DECLINED_REQUEST_STATUS = 'root:declinedRequestStatus',
  DEPARTMENTS = 'root:departments',
  ENGLISH_LANGUAGE = 'root:englishLanguage',
  HOT_POST_STATUS = 'root:hotPostStatus',
  IMAGES = 'root:images',
  LANGUAGES = 'root:languages',
  NEIGHBORHOODS = 'root:neighborhoods',
  NONE_POST_STATUS = 'root:nonePostStatus',
  POST_STATUSES = 'root:postStatuses',
  PROFESSIONS = 'root:professions',
  PURCHASE_TRANSACTION_TYPE = 'root:purchaseTransactionType',
  REQUESTED_REQUEST_STATUS = 'root:requestedRequestStatus',
  SALE_TRANSACTION_TYPE = 'root:saleTransactionType',
  SELF = 'root:self',
  SELLING_PRODUCT_STATUS = 'root:sellingProductStatus',
  SHIFTS = 'root:shifts',
  SOLD_PRODUCT_STATUS = 'root:soldProductStatus',
  SPANISH_LANGUAGE = 'root:spanishLanguage',
  TRENDING_POST_STATUS = 'root:trendingPostStatus',
  USERS = 'root:users',
  WORKERS = 'root:workers',
  WORKERS_NEIGHBORHOOD = 'root:workersNeighborhood',

  // -----------------------------------------------------------------
  // Neighborhood links
  // -----------------------------------------------------------------
  NEIGHBORHOOD_AMENITIES = 'neighborhood:amenities',
  NEIGHBORHOOD_ANNOUNCEMENTS = 'neighborhood:announcements',
  NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL = 'neighborhood:announcementsChannel',
  NEIGHBORHOOD_ATTENDANCE = 'neighborhood:attendance',
  NEIGHBORHOOD_BOOKINGS = 'neighborhood:bookings',
  NEIGHBORHOOD_CHANNELS = 'neighborhood:channels',
  NEIGHBORHOOD_COMPLAINTS = 'neighborhood:complaints',
  NEIGHBORHOOD_COMPLAINTS_CHANNEL = 'neighborhood:complaintsChannel',
  NEIGHBORHOOD_CONTACTS = 'neighborhood:contacts',
  NEIGHBORHOOD_EVENTS = 'neighborhood:events',
  NEIGHBORHOOD_FEED = 'neighborhood:feed',
  NEIGHBORHOOD_FEED_CHANNEL = 'neighborhood:feedChannel',
  NEIGHBORHOOD_LIKES = 'neighborhood:likes',
  NEIGHBORHOOD_POSTS = 'neighborhood:posts',
  NEIGHBORHOOD_POSTS2 = 'neighborhood:posts2', // templated version
  NEIGHBORHOOD_POSTS_COUNT = 'neighborhood:postsCount',
  NEIGHBORHOOD_PRODUCTS = 'neighborhood:products',
  NEIGHBORHOOD_REQUESTS = 'neighborhood:requests',
  NEIGHBORHOOD_RESOURCES = 'neighborhood:resources',
  NEIGHBORHOOD_SELF = 'neighborhood:self',
  NEIGHBORHOOD_TAGS = 'neighborhood:tags',
  NEIGHBORHOOD_USERS = 'neighborhood:users',
  NEIGHBORHOOD_WORKERS = 'neighborhood:workers',
  NEIGHBORHOOD_WORKER_CHANNEL = 'neighborhood:workerChannel',

  // -----------------------------------------------------------------
  // Worker Neighborhood Links
  // -----------------------------------------------------------------
  WORKER_NEIGHBORHOOD_CHANNELS = 'workerNeighborhood:channels',
  WORKER_NEIGHBORHOOD_EVENTS = 'workerNeighborhood:events',
  WORKER_NEIGHBORHOOD_POSTS = 'workerNeighborhood:posts',
  WORKER_NEIGHBORHOOD_POSTS2 = 'workerNeighborhood:posts2', // templated version
  WORKER_NEIGHBORHOOD_POSTS_COUNT = 'workerNeighborhood:postsCount',
  WORKER_NEIGHBORHOOD_SELF = 'workerNeighborhood:self',
  WORKER_NEIGHBORHOOD_USERS = 'workerNeighborhood:users',

  // -----------------------------------------------------------------
  // User links
  // -----------------------------------------------------------------
  USER_LANGUAGE = 'user:language',
  USER_NEIGHBORHOOD = 'user:neighborhood',
  USER_POSTS = 'user:posts',
  USER_SELF = 'user:self',
  USER_USER_IMAGE = 'user:userImage',
  USER_USER_ROLE = 'user:userRole',

  // Additional user links
  USER_WORKER = 'user:worker',
  USER_BOOKINGS = 'user:bookings',
  USER_LIKED_POSTS = 'user:likedPosts',
  USER_PURCHASES = 'user:purchases',
  USER_REQUESTS = 'user:requests',
  USER_SALES = 'user:sales',
}
