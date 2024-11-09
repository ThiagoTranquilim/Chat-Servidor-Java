
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class AceitadoraDeConexao extends Thread{

    private ServerSocket        pedido;
    private Map<String, Parceiro> usuarios;

    public AceitadoraDeConexao(String porta, Map<String, Parceiro> usuarios)
        throws Exception{

        if(porta == null)
            throw new Exception("Porta ausente");

        try{
            this.pedido = new ServerSocket(Integer.parseInt(porta));
        }
        catch (Exception erro){
            throw new Exception("Porta invalida");
        }

        if(usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.usuarios = usuarios;
    }

    public void run(){
        for(;;){

            Socket conexao = null;
            try{
                conexao = this.pedido.accept();
            }catch (Exception erro){
                continue;
            }

            String clienteId = UUID.randomUUID().toString();

            SupervisoraDeConexao supervisoraDeConexao = null;
            try{
                supervisoraDeConexao = new SupervisoraDeConexao(conexao, usuarios, clienteId);
            }catch (Exception erro){
                // sei que passei os parâmetros certos para o construtor
            }
            supervisoraDeConexao.start();
        }
    }
}
