from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

import csv

# Página de inicio de ambas fuentes a recolectar información
fonts = ['https://www.amazon.com', 'https://www.pccomponentes.com']

# Ids del campo de búsqueda
inputSearch = ['twotabsearchtextbox', 'search']
searchTerms = ['monitor gaming 27 pulgadas FHD', 'Ordenador sobremesa Gaming', 'Smartphone 5G', 'Portátil Gaming', 'Auriculares gaming', 'ratón gaming', 'teclado gaming', 'monitor gaming 27 pulgadas 4K', 'monitor gaming 27 pulgadas 2K']

options = Options()
options.add_argument('--headless')  # Ejecutar en segundo plano
options.add_argument('--no-sandbox') # Desactivar el sandbox
options.add_argument('--disable-dev-shm-usage') # Desactivar el uso de memoria compartida

# Usar el ChromeDriver con WebDriverManager
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

for font, input_search in zip(fonts, inputSearch): # Iterar sobre las fuentes y sus respectivos campos de búsqueda
    driver.get(font)
    try:
        # Buscar el campo de búsqueda
        search_box = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.ID, input_search))
        )
        
        # Enviar los términos de búsqueda
        search_box.send_keys(searchTerms[0])
        
        # Esperar a que la página cargue
        page = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.TAG_NAME, 'body'))
        )
        
        # Aquí puedes agregar el código para extraer la información relevante.
        # Ejemplo: buscar productos, extraer nombres, precios, etc.
        
    except Exception as e:
        print(f"Error en {font}: {e}")
    finally:
        driver.quit()
