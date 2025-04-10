from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

import time

# Página de inicio de ambas fuentes a recolectar información
fonts = ['https://www.pcbox.com/', 'https://www.coolmod.com/']

# Ids del campo de búsqueda
inputSearch = ['input#FastIntelligentSearchInput', 'input#dfd-searchbox-id-597y9-input']
searchTerms = ['monitor gaming 27 pulgadas FHD', 'Ordenador sobremesa Gaming']#, 'Smartphone 5G', 'Portátil Gaming', 'Auriculares gaming', 'ratón gaming', 'teclado gaming', 'monitor gaming 27 pulgadas 4K', 'monitor gaming 27 pulgadas 2K']
cardProduct = ['a.vtex-product-summary-2-x-clearLink.vtex-product-summary-2-x-clearLink--main-product-summary.h-100.flex.flex-column', 
               'a.dfd-card-link'
              ]

# Información de los artículos a extraer
info = {
    'Nombre': ['span.vtex-store-components-3-x-productBrand.vtex-store-components-3-x-productBrand--quickview', 'h1.text-2xl.font-bold'],
    'Precio': ['div.ticnova-commons-components-0-x-price', 'span.product_price.int_price'],
    'Decimal': ['', 'span.dec_price'],
    'Enlace': ['', ''],
    'Imagen': ['img.image-gallery-image', 'img.mx-auto'],
    'Valoraciones': ['', 'p.-mt3.mb-1'],
    'Fuente': ['PCBox', 'Coolmod']
}

# Configuración de Selenium
options = Options()
options.add_argument('--headless')  # Ejecutar en segundo plano sin interfaz gráfica
options.add_argument('--no-sandbox')  # Desactivar el sandbox
options.add_argument('--disable-dev-shm-usage')  # Desactivar el uso de memoria compartida

# Usar el ChromeDriver con WebDriverManager
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

def getCoolMoData():
    driver.get(fonts[1])
    WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, inputSearch[1])))
    searchInput = driver.find_element(By.CSS_SELECTOR, inputSearch[1])

    for searchTerm in searchTerms:
        searchInput.clear()
        searchInput.send_keys(searchTerm)
        searchInput.submit()

        # Esperar a que la página cargue los resultados
        WebDriverWait(driver, 10).until(EC.presence_of_all_elements_located((By.TAG_NAME, 'body')))

        # Extraer información de los productos
        products = driver.find_elements(By.CSS_SELECTOR, cardProduct[1])
        print(f'Número de productos encontrados: {len(products)}')
        for product in products:
            product.click()

            # Esperar a que la página del producto cargue
            WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.TAG_NAME, 'body')))
            try:
                name = product.find_element(By.CSS_SELECTOR, info['Nombre'][1]).text
                price = product.find_element(By.CSS_SELECTOR, info['Precio'][1])
                link = product.find_element(By.TAG_NAME, 'a').get_attribute('href')
                image = product.find_element(By.CSS_SELECTOR, info['Imagen'][1]).get_attribute('src')
                rating = product.find_element(By.CSS_SELECTOR, info['Valoraciones'][1]).text if info['Valoraciones'][1] else ''
                source = info['Fuente'][1]

                print(f'Nombre: {name}, Precio: {price}, Enlace: {link}, Imagen: {image}, Valoraciones: {rating}, Fuente: {source}')
            except Exception as e:
                print('Error al extraer datos del producto')

def getPcBoxdata():
    driver.get(fonts[0])

    for searchTerm in searchTerms:
        print(f'Realizando busqueda sobre: {searchTerm}')

        try:
            # Localizar el campo de búsqueda
            searchInput = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, inputSearch[0]))
            )
            
            # Limpiar y enviar el término de búsqueda
            searchInput.clear()
            searchInput.send_keys(searchTerm)
            searchInput.submit()

            # Esperar a que los resultados se carguen
            WebDriverWait(driver, 10).until(
                EC.presence_of_all_elements_located((By.TAG_NAME, 'body'))
            )
            time.sleep(45)

            # Extraer información de los productos
            products = driver.find_elements(By.CSS_SELECTOR, cardProduct[0])
            print(f'Número de productos encontrados: {len(products)}')

            #for product in products:
            #    try:
            #        # Extraer información del producto
            #        name = product.find_element(By.CSS_SELECTOR, info['Nombre'][0]).text
            #        price = product.find_element(By.CSS_SELECTOR, info['Precio'][0]).text
            #        link = product.find_element(By.TAG_NAME, 'a').get_attribute('href')
            #        image = product.find_element(By.CSS_SELECTOR, info['Imagen'][0]).get_attribute('src')
            #        rating = product.find_element(By.CSS_SELECTOR, info['Valoraciones'][0]).text if info['Valoraciones'][0] else ''
            #        source = info['Fuente'][0]
#
            #        print(f'Nombre: {name}, Precio: {price}, Enlace: {link}, Imagen: {image}, Valoraciones: {rating}, Fuente: {source}')
            #    except Exception as e:
            #        print(f'Error al extraer datos del producto: {e}')
        except Exception as e:
            print(f'Error al procesar el término "{searchTerm}":')  
            import traceback
            traceback.print_exc()


#getCoolMoData()
getPcBoxdata()

# Finalizar el driver
driver.quit()
