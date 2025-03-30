package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por nome
                    5 - Buscar séries por ator
                    6 - Buscar top 5 séries
                    7 - Buscar séries por categoria
                    8 - Filtrar séries
                    9 - Buscar Episódio por trecho
                    10 - Buscar top 5 episódios
                    11 - Buscar episódios a partir de uma data
                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorNome();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarPorCategoria();
                    break;
                case 8:
                    filtrarSeries();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscaPorData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscaPorData() {
        buscarSeriePorNome();
        if(serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.print("Digite o ano limite de lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodioPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorNome();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(),
                    e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodioPorTrecho() {
        System.out.print("Digite um trecho do nome para a busca: ");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                e.getSerie().getTitulo(), e.getTemporada(),
                e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void filtrarSeries() {
        System.out.print("Digite o número máximo de temporadas que a série deve ter: ");
        var quantidadeTemporadas = leitura.nextInt();
        System.out.print("Digite a avaliação mínima que a série deve ter: ");
        var avaliacao = leitura.nextDouble();
        System.out.println("Séries filtradas: ");
        List<Serie> seriesFiltradas = repositorio.seriesPorTemporadaEAvaliacao(quantidadeTemporadas, avaliacao);
        seriesFiltradas.forEach(System.out::println);
    }

    private void buscarPorCategoria() {
        System.out.print("Digite a categoria/gênero que deseja buscar: ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria: " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarTop5Series() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        System.out.println("Top 5 séries encontradas: ");
        seriesTop.forEach(s -> System.out.println(s.getTitulo() + " Avaliação: "  +s.getAvaliacao()));
    }

    private void buscarSeriePorAtor() {
        System.out.print("Digite um nome para a busca: ");
        var nomeAtor = leitura.nextLine();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCase(nomeAtor);
        System.out.println("Series em que " + nomeAtor + " atuou: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " Avaliação: "  +s.getAvaliacao()));
    }

    private void buscarSeriePorNome() {
        System.out.print("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()){
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        //dadosSeries.add(dados);
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.print("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            List<DadosTemporada> temporadas = new ArrayList<>();

            var serieEncontada = serie.get();
            for (int i = 1; i <= serieEncontada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontada.setEpisodios(episodios);
            repositorio.save(serieEncontada);
        } else {
            System.out.println("Série não encontrada!");
        }

    }
}