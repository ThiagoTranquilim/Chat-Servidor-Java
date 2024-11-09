import javax.imageio.IIOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Parceiro {

    private Socket            conexao;
    private String            nome;
    private ObjectInputStream receptor;
    private ObjectOutputStream transmissor;

    private Comunicado proximoComunicado=null;

    private Semaphore mutEx = new Semaphore(1, true);

    public Parceiro(Socket conexao,
                    ObjectInputStream receptor,
                    ObjectOutputStream transmissor,
                    String nome)
                    throws Exception{
        if(conexao == null)
            throw new Exception("Conexão ausente");

        if(receptor == null)
            throw new Exception("Receptor ausente");

        if(transmissor == null)
            throw new Exception("Transmissor ausente");

        if (nome == null || nome.trim().isEmpty())
            throw new Exception("Nome ausente");

        this.conexao = conexao;
        this.receptor = receptor;
        this.transmissor = transmissor;
        this.nome = nome;
    }

    public void receba (Comunicado x) throws Exception{

        try{
            this.transmissor.writeObject(x);
            this.transmissor.flush();
        }catch (IIOException erro){
            throw new Exception("Erro de transmissao");
        }
    }

    public Comunicado espie()throws Exception{

        try{
            this.mutEx.acquireUninterruptibly();
            if(this.proximoComunicado == null)
                this.proximoComunicado = (Comunicado)this.receptor.readObject();
            this.mutEx.release();
            return this.proximoComunicado;
        }catch (Exception erro){
            throw new Exception("Erro de recepcao");
        }
    }

    public Comunicado envie()throws Exception{

        try{
            if(this.proximoComunicado == null) this.proximoComunicado = (Comunicado) this.receptor.readObject();
            Comunicado ret = this.proximoComunicado;
            this.proximoComunicado = null;
            return ret;
        }catch (Exception erro){
            throw new Exception("Erro de recepcao");
        }
    }

    public void adeus()throws Exception{

        try{
            this.transmissor.close();
            this.receptor.close();
            this.conexao.close();
        }catch (Exception erro){
            throw new Exception("Erro de desconexao");
        }
    }

    public ObjectOutputStream getTransmissor() {
        return this.transmissor;
    }

    public ObjectInputStream getReceptor() {
        return this.receptor;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public Socket getConexao() {
        return this.conexao;
    }
}
