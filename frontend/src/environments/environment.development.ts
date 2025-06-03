export const environment = {
  production: false,

  base_url: 'http://localhost:8080/',

  sign_in: 'api/v1/user/login',
  sign_up: 'api/v1/user/register',
  update_user: 'api/v1/user/',
  check_email: 'api/v1/user/email-exist',
  get_all_users: 'api/v1/user',

  get_names_products: 'api/v1/products/get-names',
  get_all_products: 'api/v1/products/',
  get_random_products: 'api/v1/products/random',
  get_product: 'api/v1/products/',
  create_product: 'api/v1/products/',
  update_product: 'api/v1/products/',
  delete_product: 'api/v1/products/',
  get_wishlist: 'api/v1/products/wishlist',
  modify_wishlist: 'api/v1/products/wishlist/',

  get_all_categories: 'api/v1/categories/',
  get_category: 'api/v1/categories/',
  create_category: 'api/v1/categories/',
  update_category: 'api/v1/categories/',
  delete_category: 'api/v1/categories/',

  get_all_providers: 'api/v1/providers/',
  get_provider: 'api/v1/providers/',
  create_provider: 'api/v1/providers/',
  update_provider: 'api/v1/providers/',
  delete_provider: 'api/v1/providers/',

  get_cart: 'api/v1/cart/',
  add_to_cart: 'api/v1/cart/add',
  delete_product_from_cart: 'api/v1/cart/',
  modify_product_quantity: 'api/v1/cart/',

  create_order: 'api/v1/orders/',
  get_orders: 'api/v1/orders/',
  cancel_order: 'api/v1/orders/cancel/',
} as const;
