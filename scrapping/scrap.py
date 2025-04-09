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
cardProductClass = ['a-link-normal.s-line-clamp-4.s-link-style.a-text-normal', 'dfd-card-link']

# Informacion de los articulos a extraer
info = ['Nombre', 'Precio', 'Decimal', 'Enlace', 'Imagen', 'Descripción', 'Valoraciones', 'Fuente']
selectors = [
                ['span#productTitle', 'h1.text-2xl.font-bold'], 
                ['span.a-price-whole', 'span.product_price.int_price span.dec_price'],
                ['span.a-price-fraction', 'span.dec_price'],
                [], # Array vacio para el enlace, ya que no se puede obtener directamente
                ['img#landingImage', 'img.mx-auto'],
                ['div#productDescription_feature_div span', ''],
                ['span.a-size-base.review-text', 'p.-mt3.mb-1'],
                ['Amazon', 'Coolmod']
            ]



options = Options()
options.add_argument('--headless')  # Ejecutar en segundo plano sin interfaz grafica
options.add_argument('--no-sandbox') # Desactivar el sandbox
options.add_argument('--disable-dev-shm-usage') # Desactivar el uso de memoria compartida

# Usar el ChromeDriver con WebDriverManager
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

for font, input_search, product in zip(fonts, inputSearch, cardProductClass): # Iterar sobre las fuentes y sus respectivos campos de búsqueda
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
            search_box.send_keys(term)

            # Esperar a que la página cargue
            page = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.TAG_NAME, 'body'))
            )

            # Obtener los resultados en pantalla
            results = WebDriverWait(driver, 40).until(
                EC.presence_of_all_elements_located((By.CLASS_NAME, product))
            )

            for item in results:
                # Clickeo el producto
                item.click()

                # Esperar a que la página cargue
                WebDriverWait(driver, 10).until(
                    EC.presence_of_element_located((By.TAG_NAME, 'body'))
                )

                # Obtener la informacion del producto
                for dato, selector in zip(info, selectors):
                    try:
                        if selector == []: # Enlace
                            link = driver.current_url
                            data = link
                        else:
                            data = driver.find_element(By.CSS_SELECTOR, selector[0] if font == fonts[0] else selector[1])
                    except Exception as e:
                        print(f"Error al obtener {dato} de {font}")
                        data = 'No disponible'

                    # Guardar la información en un CSV con el nombre de la fuente
                    with open(f'{font.split(".")[1]}.csv', 'a', newline='', encoding='utf-8') as file:
                        writer = csv.writer(file)
                        writer.writerow([dato, data])
        
        print(f"Data de {font} cargada correctamente.")
    except Exception as e:
        print(f"Error al leer la página {font}", e)
        import traceback
        traceback.print_exc()

driver.quit()
