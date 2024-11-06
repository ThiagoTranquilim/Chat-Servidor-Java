import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {

    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;

    public static void main(String[] args) throws Exception {

        if(args.length > 2){
            System.err.println("Uso esperado: java Cliente [HOST [PORTA]]\n");;
            return;
        }

        Socket conexao = null;

        try{
            String host = Cliente.HOST_PADRAO;
            int porta = Cliente.PORTA_PADRAO;

            if(args.length > 0)
                host = args[0];

            if(args.length == 2)
                porta = Integer.parseInt(args[1]);

            conexao = new Socket(host, porta);
        }catch (Exception erro){
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        ObjectOutputStream transmissor = null;
        try{
            transmissor = new ObjectOutputStream(conexao.getOutputStream());
        }catch (Exception erro){
            System.err.println("Indique o servidor e a porta corretos\n");
            return;
        }

        ObjectInputStream receptor = null;
        try{
            receptor = new ObjectInputStream(conexao.getInputStream());
        }catch (Exception erro){
            System.err.println("Indique o servidor e a porta corretos");
            return;
        }

        Parceiro servidor = null;

        try{
            servidor = new Parceiro(conexao, receptor, transmissor);
        }catch (Exception erro){
            System.err.println("Indique o servidor e a porta corretos");
            return;
        }

        TratadoraDeComunicacao tratadoraDeComunicacao = null;

        try{
            tratadoraDeComunicacao = new TratadoraDeComunicacao(servidor);
        }catch (Exception erro){
        }

        tratadoraDeComunicacao.start();

        String mensagem = null;
        do {
            System.out.print("> ");

            try {
                mensagem = Teclado.getUmString();
            } catch (Exception erro) {
                System.err.println("Erro ao ler a mensagem.");
            }

            if (mensagem != null && !mensagem.equalsIgnoreCase("sair")) {
                servidor.receba(new Mensagem(mensagem));
            }

        } while (mensagem != null && !mensagem.equalsIgnoreCase("sair"));

        try {

            servidor.receba(new PedidoParaSair());
            System.out.println("Cliente desconectado.");
        } catch (Exception erro) {
            System.err.println("Erro ao finalizar a conexão.");
        }
    }
}
