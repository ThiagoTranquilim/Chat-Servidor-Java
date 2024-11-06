import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SupervisoraDeConexao extends Thread {

    private double              valor = 0;
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios)
        throws Exception{

        if(conexao == null)
            throw new Exception("Conexao ausente");

        if(usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.conexao = conexao;
        this.usuarios = usuarios;
    }

    public void run(){

        ObjectOutputStream transmissor;
        try{
            transmissor =
            new ObjectOutputStream(
            this.conexao.getOutputStream());
        }catch (Exception erro){
            return;
        }

        ObjectInputStream receptor;
        try{
            receptor =
            new ObjectInputStream(
            this.conexao.getInputStream());

        }catch (Exception err0){

            try{
                transmissor.close();
            }
            catch (Exception falha){
                // so tentando fechar antes de acabar a thread
            }
            return;
        }

        try{
            this.usuario  = new Parceiro(conexao, receptor, transmissor);
        }catch (Exception erro){
            // sei que passei os parametros certos
        }

        try{

            synchronized (this.usuarios){

                this.usuarios.add(this.usuario);
            }

            for(;;){
                Comunicado comunicado = this.usuario.envie();

                if(comunicado == null)
                    return;

                else if(comunicado instanceof Mensagem){
                    Mensagem mensagem = (Mensagem) comunicado;
                    System.out.println(mensagem.getConteudo());

                    synchronized (this.usuario){
                        for(Parceiro cliente : this.usuarios){

                            if(cliente != this.usuario){

                                cliente.receba(mensagem);

                            }
                        }
                    }
                }else if(comunicado instanceof PedidoParaSair){
                    synchronized (this.usuarios){
                        this.usuarios.remove(this.usuario);
                    }
                    this.usuario.adeus();
                }
            }
        }catch (Exception erro){
            try{
                transmissor.close();
                receptor.close();
            } catch (Exception falha) {
                // so tentando fechar antes de acabar a thread
            }
            return;
        }
    }
}
