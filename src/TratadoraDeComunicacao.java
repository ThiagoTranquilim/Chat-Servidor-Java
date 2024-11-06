public class TratadoraDeComunicacao extends Thread {

    private Parceiro servidor;

    public TratadoraDeComunicacao(Parceiro servidor)throws Exception{

        if(servidor == null)
            throw new Exception("Porta invalida");

        this.servidor = servidor;
    }

    public void run(){

        for(;;){

            try {
                if (this.servidor.espie() instanceof ComunicadoDeDesligamento) {

                    System.out.println("\nO servidor vai ser desligado agora;");
                    System.err.println("volte mais tarde!\n");
                    System.exit(0);
                }
                else if(this.servidor.espie() instanceof Mensagem){
                    Mensagem mensagem = (Mensagem) this.servidor.envie();
                    System.out.println(mensagem.getConteudo());
                    System.out.print("> ");
                }
            }catch (Exception erro){
            }
        }
    }
}
