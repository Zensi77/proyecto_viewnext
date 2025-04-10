from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from selenium.common.exceptions import StaleElementReferenceException
import time
import json

# Configuración de Selenium
options = Options()
options.add_argument('--headless')  # Ejecutar en segundo plano sin interfaz gráfica
options.add_argument('--no-sandbox')  # Desactivar el sandbox
options.add_argument('--disable-dev-shm-usage')  # Desactivar el uso de memoria compartida

# Usar el ChromeDriver con WebDriverManager
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

# Cargar el archivo JSON
with open('properties.json', 'r') as f:
    data = json.load(f)

# Función para manejar la espera y la interacción con los elementos
def interact_with_search_input(font, searchTerm):
    try:
        # Localizar el campo de búsqueda
        searchInput = WebDriverWait(driver, 30).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, data['inputSearch'][font]))
        )
        
        # Limpiar y enviar el término de búsqueda
        searchInput.clear()
        searchInput.send_keys(searchTerm)
        searchInput.submit() if font == 'pcbox' else None  # Enviar el formulario si es necesario

        WebDriverWait(driver, 30).until(
            EC.presence_of_element_located((By.TAG_NAME, 'body'))
        )
        print(f'Buscando: {searchTerm}')

    except StaleElementReferenceException:
        print("Search input obsoleto. Reintentando...")
        interact_with_search_input(font, searchTerm)  # Intentar nuevamente si el elemento es obsoleto

def saveOnDatabase(data):
    # Guardar datos en la base de datos
    print(f"Guardando datos: {data}")

cookiesAccepted = False
# Iterar sobre las fuentes
for font in data['fonts']:
    print(f"Scraping en la fuente: {font}")

    for key, searchTerm in data['searchterms'].items():  # Iterar sobre los términos de búsqueda
        time.sleep(10)  # Espera breve antes de cada búsqueda
        driver.get(data['fonts'][font])  # Cargar la página de la fuente
        
        # Aceptar cookies si es necesario
        if(font == 'pcbox' and not cookiesAccepted):
            cookies = WebDriverWait(driver, 30).until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, 'div#cookiescript_accept'))
            )
            cookies.click() if cookies else None
            cookiesAccepted = True
            print("Cookies aceptadas.")
        elif(font == 'coolmod' and not cookiesAccepted):
            cookies = WebDriverWait(driver, 30).until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, 'button#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll'))
            )
            cookies.click() if cookies else None
            cookiesAccepted = True
            print("Cookies aceptadas.")

        try:
            # Interactuar con el campo de búsqueda
            interact_with_search_input(font, searchTerm)

            # Esperar a que los resultados se carguen
            WebDriverWait(driver, 30).until(
                EC.presence_of_all_elements_located((By.CSS_SELECTOR, data['cardProduct'][font])))

            print('Resultados cargados correctamente, realizando recuento...')

            # Extraer información de los productos
            products = driver.find_elements(By.CSS_SELECTOR, data['cardProduct'][font])
            print('------------------------------------------')
            print(f'Número de productos encontrados: {len(products)}')
            print('------------------------------------------')

            for product in products:
                # Diccionario vacío para almacenar los datos del artículo
                data_articulo = {
                    "provider": 1 if font == 'pcbox' else 2,
                }

                for key, selector in data['data'].items():
                    # key = "imagen" o "enlace" o "nombre" o "precio"
                    # selector = Objeto JSON con los selectores CSS
                    # selector = data['data'][key][font]
                    try:
                        # Verificar si el selector no está vacío
                        if selector[font]:
                            if key == "imagen":
                                # Obtener el atributo 'src' para imágenes
                                data_articulo[key] = product.find_element(By.CSS_SELECTOR, selector[font]).get_attribute('src')
                            elif key == "enlace":
                                # Usar la URL actual para el enlace
                                data_articulo[key] = driver.current_url
                            else:
                                # Obtener el texto del elemento
                                data_articulo[key] = product.find_element(By.CSS_SELECTOR, selector[font]).text
                        else:
                            data_articulo[key] = None  # Si el selector está vacío, asignar None
                            
                    except Exception as e:
                        print(f"Error al extraer {key}: {e}")
                        data_articulo[key] = None  # Asignar None si ocurre un error

                # Guardar los datos del artículo en la base de datos
                saveOnDatabase(data_articulo)
        except Exception as e:
            print(f'Error al cargar la página: {font} - {searchTerm}')

            screenshot_path = f"/errors/screenshot_error_{font}_{key}.png"
            driver.save_screenshot(screenshot_path)
            print(f"Captura de pantalla guardada en: {screenshot_path}")
            print(e)
    
    cookiesAccepted = False  # Reiniciar la aceptación de cookies para la siguiente fuente

# Cerrar el navegador después de terminar
driver.quit()
