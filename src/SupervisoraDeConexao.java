import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class SupervisoraDeConexao extends Thread {

    private Parceiro usuario;
    private Socket conexao;
    private Map<String, Parceiro> usuarios;

    public SupervisoraDeConexao(Socket conexao, Map<String, Parceiro> usuarios, String nomeUsuario)
            throws Exception {

        if (conexao == null)
            throw new Exception("Conexao ausente");

        if (usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.conexao = conexao;
        this.usuarios = usuarios;

        ObjectOutputStream transmissor = new ObjectOutputStream(conexao.getOutputStream());
        ObjectInputStream receptor = new ObjectInputStream(conexao.getInputStream());
        this.usuario = new Parceiro(conexao, receptor, transmissor, nomeUsuario);
    }

    public void run() {
        try {

            Comunicado comunicado = this.usuario.envie();
            if(comunicado instanceof Mensagem){
                Mensagem mensagem = (Mensagem) comunicado;
                String conteudo = mensagem.getConteudo();

                if(conteudo.startsWith("LOGIN:")){
                    String nomeUsuario = conteudo.substring(6).trim();
                    this.usuario.setNome(nomeUsuario);

                    synchronized (usuarios){
                        usuarios.put(nomeUsuario, this.usuario);
                    }
                    System.out.println("Usuário " + nomeUsuario + " conectado");
                }else{
                    System.out.println("Formato de login inadequado");
                }
            }

            for (;;) {
                comunicado = this.usuario.envie();

                if (comunicado == null)
                    return;

                else if (comunicado instanceof Mensagem) {
                    Mensagem mensagem = (Mensagem) comunicado;
                    String conteudo = mensagem.getConteudo();

                    if (conteudo.startsWith("@")) {
                        String[] partes = conteudo.split(" ", 2);
                        String nomeUsuario = partes[0].substring(1);
                        String mensagemConteudo = partes.length > 1 ? partes[1] : "";

                        Parceiro destinatario = usuarios.get(nomeUsuario);
                        if (destinatario != null) {
                            destinatario.receba(new Mensagem("De: " + usuario.getNome() + ": " + mensagemConteudo));
                        } else {
                            usuario.receba(new Mensagem("Usuário: " + nomeUsuario + " não encontrado"));
                        }
                    } else {
                        // Envia mensagem para todos os outros clientes
                        synchronized (this.usuarios) {
                            for (Parceiro cliente : this.usuarios.values()) {
                                if (cliente != this.usuario) {
                                    cliente.receba(new Mensagem(usuario.getNome() + ": " + conteudo));
                                }
                            }
                        }
                    }
                } else if (comunicado instanceof PedidoParaSair) {
                    synchronized (this.usuarios) {
                        this.usuarios.remove(usuario.getNome());
                    }
                    this.usuario.adeus();
                    return;
                }
            }
        } catch (Exception erro) {
            erro.printStackTrace();
            try {
                usuario.getTransmissor().close();
                usuario.getReceptor().close();
                conexao.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
