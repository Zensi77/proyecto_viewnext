from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

import csv

# Página de inicio de ambas fuentes a recolectar información
fonts = ['https://www.amazon.com/', 'https://www.coolmod.com/']

# Ids del campo de búsqueda
inputSearch = ['twotabsearchtextbox', 'seek']
searchTerms = ['monitor gaming 27 pulgadas FHD', 'Ordenador sobremesa Gaming', 'Smartphone 5G', 'Portátil Gaming', 'Auriculares gaming', 'ratón gaming', 'teclado gaming', 'monitor gaming 27 pulgadas 4K', 'monitor gaming 27 pulgadas 2K']
cardProductClass = ['a-link-normal s-line-clamp-4 s-link-style a-text-normal', 'dfd-card-link']

# Informacion de los articulos a extraer
info = ['Nombre', 'Precio', 'Enlace', 'Imagen', 'Descripción', 'Valoraciones']



options = Options()
options.add_argument('--headless')  # Ejecutar en segundo plano
options.add_argument('--no-sandbox') # Desactivar el sandbox
options.add_argument('--disable-dev-shm-usage') # Desactivar el uso de memoria compartida

# Usar el ChromeDriver con WebDriverManager
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

for font, input_search, products in zip(fonts, inputSearch, cardProductClass): # Iterar sobre las fuentes y sus respectivos campos de búsqueda
    driver.get(font)
    try:
        # Buscar el campo de búsqueda, esperando a que esté presente
        search_box = WebDriverWait(driver, 30).until(
            EC.presence_of_element_located((By.ID, input_search))
        )

        for term in searchTerms:
            # Limpiar el campo de búsqueda antes de enviar un nuevo término
            search_box.clear()

            # Enviar los términos de búsqueda
            search_box.send_keys(searchTerms[0])

            # Esperar a que la página cargue
            page = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.TAG_NAME, 'body'))
            )

            # Obtener los 10 primeros resultados de búsqueda
            results = WebDriverWait(driver, 10).until(
                EC.presence_of_all_elements_located((By.CLASS_NAME, products))
            )

            for item in results:
                #navego al href del producto
                item.click()

                # Esperar a que la página cargue
                WebDriverWait(driver, 10).until(
                    EC.presence_of_element_located((By.TAG_NAME, 'body'))
                )

            # Guardar los resultados en un archivo CSV

        
        # Aquí puedes agregar el código para extraer la información relevante.
        # Ejemplo: buscar productos, extraer nombres, precios, etc.
        

        print(f"Página {font} cargada correctamente.")
    except Exception as e:
        print(f"Error en al leer la página {font}")
        import traceback
        traceback.print_exc()

driver.quit()
