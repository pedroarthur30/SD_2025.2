import requests
import json

# URL do seu servidor Kotlin
BASE_URL = "http://localhost:8080/carrinho"

def main():
    print("--- [PYTHON] Iniciando Cliente ---")

    # 1. Cria um produto (JSON)
    meu_produto = {
        "id": "PY-001",
        "nome": "Curso de Python",
        "preco": 150.00,
        "peso": 0.5,
        "sku": "LIVRO-PY"
    }

    # 2. Envia para o servidor (POST)
    print(f"-> Enviando: {meu_produto['nome']}")
    try:
        resp = requests.post(BASE_URL, json=meu_produto)
        print(f"   Status: {resp.status_code}")
        print(f"   Servidor respondeu: {resp.text}")
    except Exception as e:
        print(f"Erro ao conectar: {e}")

    # 3. Pede a lista para conferir (GET)
    print("\n-> Conferindo o carrinho no servidor...")
    resp = requests.get(BASE_URL)
    lista = resp.json()

    print(f"   Itens encontrados: {len(lista)}")
    for item in lista:
        print(f"   * {item['nome']} - R$ {item['preco']}")

    # 4. Finaliza a compra (POST)
    print("\n-> Finalizando compra...")
    resp = requests.post(f"{BASE_URL}/finalizar")
    print(f"   Resultado: {resp.json()}")

if __name__ == "__main__":
    main()