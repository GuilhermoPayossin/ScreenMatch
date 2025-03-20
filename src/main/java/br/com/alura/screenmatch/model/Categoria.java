package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime"),
    ANIMACAO("Animation", "Animação");

    private String categoriaOmdb;

    private String categoriaEmPortugues;

    Categoria(String categoriaOmdb, String categoriaEmPortugues) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaEmPortugues = categoriaEmPortugues;
    }

    public static Categoria fromPortuges(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaEmPortugues.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
