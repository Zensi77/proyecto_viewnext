export const environment = {
  production: true,

  base_url: 'http://54.84.71.194:30080/api/v1/',

  sign_in: 'user/login',
  sign_up: 'user/register',
  update_user: 'user/',
  check_email: 'user/email-exist',
  get_all_users: 'api/v1/user',

  get_names_products: 'products/get-names',
  get_all_products: 'products/',
  get_random_products: 'products/random',
  get_product: 'products/',
  create_product: 'products/',
  update_product: 'products/',
  delete_product: 'products/',
  get_wishlist: 'products/wishlist',
  modify_wishlist: 'products/wishlist/',

  get_all_categories: 'categories/',
  get_category: 'categories/',
  create_category: 'categories/',
  update_category: 'categories/',
  delete_category: 'categories/',

  get_all_providers: 'providers/',
  get_provider: 'providers/',
  create_provider: 'providers/',
  update_provider: 'providers/',
  delete_provider: 'providers/',

  get_cart: 'cart/',
  add_to_cart: 'cart/add',
  delete_product_from_cart: 'cart/',
  modify_product_quantity: 'cart/',

  create_order: 'orders/',
  get_orders: 'orders/',
  cancel_order: 'orders/cancel/',
} as const;
