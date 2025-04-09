driver.get(url): Navega a la URL especificada.

driver.title: Obtiene el título de la página actual.

driver.current_url: Obtiene la URL actual de la página.

driver.page_source: Obtiene el código HTML de la página actual.

driver.find_element(By.ID, "id"): Busca un elemento por su ID.

driver.find_element(By.XPATH, "xpath"): Busca un elemento por su XPath.

driver.find_element(By.CSS_SELECTOR, "selector"): Busca un elemento por su selector CSS.

driver.find_element(By.TAG_NAME, "tag"): Busca un elemento por su nombre de etiqueta.

driver.find_elements(By.ID, "id"): Busca todos los elementos que coinciden con el ID especificado.

driver.find_elements(By.XPATH, "xpath"): Busca todos los elementos que coinciden con el XPath especificado.

driver.find_elements(By.CSS_SELECTOR, "selector"): Busca todos los elementos que coinciden con el selector CSS especificado.

driver.find_elements(By.TAG_NAME, "tag"): Busca todos los elementos que coinciden con el nombre de etiqueta especificado.


Para sacar la URL de una página, puedes utilizar el método driver.current_url.


Aquí te dejo algunos métodos adicionales que pueden ser útiles:



driver.back(): Vuelve a la página anterior.

driver.forward(): Avanza a la página siguiente.

driver.refresh(): Recarga la página actual.

driver.quit(): Cierra el navegador.

driver.close(): Cierra la ventana actual del navegador.

driver.switch_to.window(window_handle): Cambia a la ventana especificada.

driver.switch_to.frame(frame_reference): Cambia al marco especificado.

driver.switch_to.alert: Cambia al cuadro de diálogo de alerta.

driver.switch_to.default_content(): Vuelve al contenido por defecto.


También puedes utilizar métodos para interactuar con los elementos de la página, como:



element.click(): Hace clic en el elemento.

element.send_keys("texto"): Envía texto al elemento.

element.clear(): Borra el texto del elemento.

element.get_attribute("atributo"): Obtiene el valor del atributo especificado.

element.get_property("propiedad"): Obtiene el valor de la propiedad especificada.