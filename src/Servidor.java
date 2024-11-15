import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Servidor {

    public static String PORTA_PADRAO = "3000";

    public static void main (String[] args){

        if(args.length > 1){
            System.err.println("Uso esperado: java Servidor [PORTA]\n");
            return;
        }

        String porta = Servidor.PORTA_PADRAO;

        if(args.length == 1)
            porta = args[0];

        Map<String, Parceiro> usuarios = new HashMap<>();

        AceitadoraDeConexao aceitadoraDeConexao = null;

        try{
            aceitadoraDeConexao = new AceitadoraDeConexao(porta, usuarios);
            aceitadoraDeConexao.start();

        }catch (Exception erro){
            System.err.println("Escolha uma porta apropriada e liberada para o uso!\n");
            return;
        }

        for(;;){
            System.out.println ("O servidor esta ativo! Para desativa-lo,");
            System.out.println ("use o comando \"desativar\"\n");
            System.out.print   ("> ");

            String comando = null;
            try{
                comando = Teclado.getUmString();
            }catch (Exception erro){
            }

            if(comando.toLowerCase().equals("desativar")){

                synchronized (usuarios){
                    ComunicadoDeDesligamento comunicadoDeDesligamento = new ComunicadoDeDesligamento();

                    for(Parceiro usuario:usuarios.values()){

                        try{
                            usuario.receba(comunicadoDeDesligamento);
                            usuario.adeus();
                        }catch (Exception erro){

                        }
                    }
                }

                System.out.println ("O servidor foi desativado!\n");
                System.exit(0);

            }else if(comando.toLowerCase().equals("listar")){
                synchronized (usuarios) {
                    System.out.println("Clientes conectados:");
                    for (Map.Entry<String, Parceiro> entry : usuarios.entrySet()) {
                        String nome = entry.getKey();
                        Parceiro parceiro = entry.getValue();
                        String enderecoIP = parceiro.getConexao().getInetAddress().getHostAddress();
                        System.out.println("Usuário: " + nome + " | IP: " + enderecoIP);
                    }
                }
            }else{
                System.err.println ("Comando invalido!\n");
            }
        }
    }
}
