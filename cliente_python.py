import requests
import json

# URL do seu servidor Kotlin
BASE_URL = "http://localhost:8080"

def listar_produtos_disponiveis():
    """Lista todos os produtos disponíveis na loja"""
    print("\n=== PRODUTOS DISPONÍVEIS ===")
    try:
        resp = requests.get(f"{BASE_URL}/produtos")
        produtos = resp.json()
        
        if not produtos:
            print("Nenhum produto disponível!")
            return
        
        print(f"\nTotal de produtos: {len(produtos)}")
        print("\n" + "-"*80)
        
        for i, produto in enumerate(produtos, 1):
            print(f"\n{i}. [{produto['id']}] {produto['nome']}")
            print(f"   Preço: R$ {produto['preco']:.2f}")
            
            # Identifica o tipo pelo conteúdo
            if 'peso' in produto:
                print(f"   Tipo: Produto Físico")
                print(f"   Peso: {produto['peso']} kg | SKU: {produto['sku']}")
            elif 'urlDownload' in produto:
                print(f"   Tipo: Produto Digital")
                print(f"   Download: {produto['urlDownload']}")
            elif 'duracaoHoras' in produto:
                print(f"   Tipo: Serviço")
                print(f"   Duração: {produto['duracaoHoras']} hora(s)")
            
        print("\n" + "-"*80)
    except Exception as e:
        print(f"✗ Erro ao listar produtos: {e}")

def buscar_produto_por_id():
    """Busca um produto específico por ID"""
    print("\n=== BUSCAR PRODUTO ===")
    id_produto = input("Digite o ID do produto: ")
    
    try:
        resp = requests.get(f"{BASE_URL}/produtos/buscar?id={id_produto}")
        if resp.status_code == 200:
            produto = resp.json()
            print(f"\n✓ Produto encontrado:")
            print(f"   Nome: {produto['nome']}")
            print(f"   Preço: R$ {produto['preco']:.2f}")
            
            if 'peso' in produto:
                print(f"   Peso: {produto['peso']} kg")
                print(f"   SKU: {produto['sku']}")
            elif 'urlDownload' in produto:
                print(f"   URL: {produto['urlDownload']}")
            elif 'duracaoHoras' in produto:
                print(f"   Duração: {produto['duracaoHoras']}h")
        else:
            print(f"✗ Produto não encontrado!")
    except Exception as e:
        print(f"✗ Erro ao buscar: {e}")

def filtrar_produtos_por_tipo():
    """Filtra produtos por tipo"""
    print("\n=== FILTRAR POR TIPO ===")
    print("1. Produtos Físicos")
    print("2. Produtos Digitais")
    print("3. Serviços")
    
    opcao = input("Escolha o tipo: ")
    
    tipo_map = {
        "1": "fisico",
        "2": "digital",
        "3": "servico"
    }
    
    tipo = tipo_map.get(opcao)
    if not tipo:
        print("✗ Opção inválida!")
        return
    
    try:
        resp = requests.get(f"{BASE_URL}/produtos/tipo?tipo={tipo}")
        produtos = resp.json()
        
        print(f"\n✓ Encontrados {len(produtos)} produto(s):")
        for produto in produtos:
            print(f"   [{produto['id']}] {produto['nome']} - R$ {produto['preco']:.2f}")
    except Exception as e:
        print(f"✗ Erro ao filtrar: {e}")

def adicionar_do_catalogo():
    """Adiciona um produto do catálogo ao carrinho"""
    print("\n=== ADICIONAR DO CATÁLOGO ===")
    id_produto = input("Digite o ID do produto: ")
    
    try:
        resp = requests.post(f"{BASE_URL}/carrinho/adicionar?id={id_produto}")
        if resp.status_code == 201:
            print("✓ Produto adicionado ao carrinho!")
        elif resp.status_code == 404:
            print("✗ Produto não encontrado!")
        else:
            print(f"✗ Erro: {resp.text}")
    except Exception as e:
        print(f"✗ Erro ao adicionar: {e}")

def adicionar_produto():
    """Adiciona um produto customizado ao carrinho"""
    print("\n=== ADICIONAR PRODUTO CUSTOMIZADO ===")
    print("Tipo de produto: 1 - Físico, 2 - Digital, 3 - Serviço")
    tipo = input("Escolha o tipo: ")
    
    id_produto = input("ID do produto: ")
    nome = input("Nome: ")
    preco = float(input("Preço: "))
    
    produto = {
        "id": id_produto,
        "nome": nome,
        "preco": preco
    }
    
    if tipo == "1":  # Produto Físico
        peso = float(input("Peso (kg): "))
        sku = input("SKU: ")
        produto["peso"] = peso
        produto["sku"] = sku
    elif tipo == "2":  # Produto Digital
        url = input("URL de download: ")
        produto["urlDownload"] = url
    elif tipo == "3":  # Serviço
        horas = int(input("Duração (horas): "))
        produto["duracaoHoras"] = horas
    
    try:
        resp = requests.post(f"{BASE_URL}/carrinho", json=produto)
        print(f"\n✓ Status: {resp.status_code}")
        print(f"✓ {resp.json()['mensagem']}")
    except Exception as e:
        print(f"✗ Erro ao conectar: {e}")

def listar_produtos():
    """Lista todos os produtos no carrinho"""
    print("\n=== MEU CARRINHO ===")
    try:
        resp = requests.get(f"{BASE_URL}/carrinho")
        lista = resp.json()
        
        if not lista:
            print("Carrinho vazio!")
            return
        
        print(f"\nItens no carrinho: {len(lista)}")
        total = 0
        
        for i, item in enumerate(lista, 1):
            print(f"\n{i}. {item['nome']}")
            print(f"   Preço: R$ {item['preco']:.2f}")
            total += item['preco']
            
            if 'peso' in item:
                print(f"   Peso: {item['peso']} kg | SKU: {item['sku']}")
            if 'urlDownload' in item:
                print(f"   Download: {item['urlDownload']}")
            if 'duracaoHoras' in item:
                print(f"   Duração: {item['duracaoHoras']}h")
        
        print(f"\n{'='*50}")
        print(f"TOTAL: R$ {total:.2f}")
        print(f"{'='*50}")
    except Exception as e:
        print(f"✗ Erro ao listar: {e}")

def finalizar_compra():
    """Finaliza a compra"""
    print("\n=== FINALIZAR COMPRA ===")
    confirmacao = input("Tem certeza que deseja finalizar? (s/n): ")
    
    if confirmacao.lower() == 's':
        try:
            resp = requests.post(f"{BASE_URL}/carrinho/finalizar")
            resultado = resp.json()
            print(f"\n✓ {resultado['mensagem']}")
            print(f"✓ Total pago: R$ {resultado['total']:.2f}")
        except Exception as e:
            print(f"✗ Erro ao finalizar: {e}")
    else:
        print("Compra cancelada!")

def limpar_carrinho():
    """Limpa o carrinho"""
    print("\n=== LIMPAR CARRINHO ===")
    confirmacao = input("Tem certeza? (s/n): ")
    
    if confirmacao.lower() == 's':
        try:
            resp = requests.delete(f"{BASE_URL}/carrinho")
            print(f"✓ {resp.json()['mensagem']}")
        except Exception as e:
            print(f"✗ Erro: {e}")
    else:
        print("Operação cancelada!")

def exibir_menu():
    """Exibe o menu principal"""
    print("\n" + "="*60)
    print("    CARRINHO DE COMPRAS - CLIENTE PYTHON")
    print("="*60)
    print("CATÁLOGO DE PRODUTOS:")
    print("  1. Listar produtos disponíveis")
    print("  2. Buscar produto por ID")
    print("  3. Filtrar produtos por tipo")
    print("  4. Adicionar produto do catálogo ao carrinho")
    print("\nCARRINHO:")
    print("  5. Ver meu carrinho")
    print("  6. Adicionar produto customizado")
    print("  7. Limpar carrinho")
    print("  8. Finalizar compra")
    print("\n  9. Sair")
    print("="*60)

def main():
    print("--- [PYTHON] Iniciando Cliente ---")
    
    while True:
        exibir_menu()
        opcao = input("\nEscolha uma opção: ")
        
        if opcao == "1":
            listar_produtos_disponiveis()
        elif opcao == "2":
            buscar_produto_por_id()
        elif opcao == "3":
            filtrar_produtos_por_tipo()
        elif opcao == "4":
            adicionar_do_catalogo()
        elif opcao == "5":
            listar_produtos()
        elif opcao == "6":
            adicionar_produto()
        elif opcao == "7":
            limpar_carrinho()
        elif opcao == "8":
            finalizar_compra()
        elif opcao == "9":
            print("\nEncerrando cliente... Até logo!")
            break
        else:
            print("\n✗ Opção inválida! Tente novamente.")
        
        input("\nPressione ENTER para continuar...")

if __name__ == "__main__":
    main()
