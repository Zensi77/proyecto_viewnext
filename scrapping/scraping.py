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
        searchInput.submit()
        print(f'Buscando: {searchTerm}')

    except StaleElementReferenceException:
        print("Elemento obsoleto. Reintentando...")
        interact_with_search_input(font, searchTerm)  # Intentar nuevamente si el elemento es obsoleto

cookiesAccepted = False
# Iterar sobre las fuentes
for font in data['fonts']:
    print(f"Scraping en la fuente: {font}")

    for key, searchTerm in data['searchterms'].items():  # Iterar sobre los términos de búsqueda
        time.sleep(10)  # Espera breve antes de cada búsqueda
        driver.get(data['fonts'][font])  # Cargar la página de la fuente

        if(font == 'pcbox' and not cookiesAccepted):
            cookies = WebDriverWait(driver, 20).until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, 'div#cookiescript_accept'))
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
            print(f'Número de productos encontrados: {len(products)}')

            # Puedes agregar aquí más código para extraer detalles específicos de cada producto si lo necesitas

        except Exception as e:
            print(f'Error al cargar la página:')
            screenshot_path = f"screenshot_error_{font}_{key}.png"
            driver.save_screenshot(screenshot_path)
            print(f"Captura de pantalla guardada en: {screenshot_path}")
            print(e)

# Cerrar el navegador después de terminar
driver.quit()
