export const environment = {
  production: false,

  sign_in: 'http://localhost:8080/api/v1/user/login',
  sign_up: 'http://localhost:8080/api/v1/user/register',
  register_admin: 'http://localhost:8080/api/v1/user/register-admin',
  check_email: 'http://localhost:8080/api/v1/user/email-exist',

  get_names_products: 'http://localhost:8080/api/v1/products/get-names',
  get_all_products: 'http://localhost:8080/api/v1/products/',
  get_random_products: 'http://localhost:8080/api/v1/products/random',
  get_product: 'http://localhost:8080/api/v1/products/',
  create_product: 'http://localhost:8080/api/v1/products/',
  update_product: 'http://localhost:8080/api/v1/products/',
  delete_product: 'http://localhost:8080/api/v1/products/',

  get_all_categories: 'http://localhost:8080/api/v1/categories/',
  get_category: 'http://localhost:8080/api/v1/categories/',
  create_category: 'http://localhost:8080/api/v1/categories/',
  update_category: 'http://localhost:8080/api/v1/categories/',
  delete_category: 'http://localhost:8080/api/v1/categories/',

  get_all_providers: 'http://localhost:8080/api/v1/providers/',
  get_provider: 'http://localhost:8080/api/v1/providers/',
  create_provider: 'http://localhost:8080/api/v1/providers/',
  update_provider: 'http://localhost:8080/api/v1/providers/',
  delete_provider: 'http://localhost:8080/api/v1/providers/',

  get_cart: 'http://localhost:8080/api/v1/cart/',
  add_to_cart: 'http://localhost:8080/api/v1/cart/add',
  delete_product_from_cart: 'http://localhost:8080/api/v1/cart/',
  modify_product_quantity: 'http://localhost:8080/api/v1/cart/',

  create_order: 'http://localhost:8080/api/v1/orders/',
  get_orders: 'http://localhost:8080/api/v1/orders/',
  cancel_order: 'http://localhost:8080/api/v1/orders/cancel/',
};
