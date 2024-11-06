public class Mensagem extends Comunicado {
    private String conteudo;

    public Mensagem(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getConteudo() {
        return this.conteudo;
    }
}
