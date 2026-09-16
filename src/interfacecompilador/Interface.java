
package interfacecompilador;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Kauã, Luig e Artur
 */
public class Interface extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Dimension TAMANHO_BOTAO = new Dimension(150, 86);
    private static final String MENSAGEM_COMPILAR = "compilação de programas ainda não foi implementada";
    private static final String MENSAGEM_EQUIPE = "Kauã\nLuig\nArtur";

    private final JToolBar barraFerramentas = new JToolBar(JToolBar.VERTICAL);
    private final JTextArea editor = new JTextArea();
    private final JTextArea mensagens = new JTextArea();
    private final JLabel barraStatus = new JLabel();
    private final JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    /**
     * Arquivo aberto/salvo no momento. Nulo enquanto o arquivo for novo.
     */
    private File arquivo;

    public Interface() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1500, 800);

        montarBarraFerramentas();
        montarEditor();
        montarAreaMensagens();
        montarBarraStatus();

        divisao.setTopComponent(new JScrollPane(editor,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS));
        divisao.setBottomComponent(new JScrollPane(mensagens,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS));
        divisao.setDividerSize(8);
        divisao.setDividerLocation(520);

        ((JScrollPane) divisao.getTopComponent()).setRowHeaderView(new NumeroDeLinhas(editor));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(barraFerramentas, BorderLayout.WEST);
        getContentPane().add(divisao, BorderLayout.CENTER);
        getContentPane().add(barraStatus, BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------------ componentes

    private void montarBarraFerramentas() {
        barraFerramentas.setFloatable(false);
        barraFerramentas.setBorder(BorderFactory.createEmptyBorder());
        barraFerramentas.setPreferredSize(new Dimension(150, 0));

        criarBotao("novo [ctrl-n]", "Novo.png",
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), evt -> novo());
        criarBotao("abrir [ctrl-o]", "Abrir.png",
                KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), evt -> abrir());
        criarBotao("salvar [ctrl-s]", "Salvar.png",
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), evt -> salvar());
        criarBotao("copiar [ctrl-c]", "Copiar.png",
                KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), evt -> editor.copy());
        criarBotao("colar [ctrl-v]", "Colar.png",
                KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), evt -> editor.paste());
        criarBotao("recortar [ctrl-x]", "Recortar.png",
                KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), evt -> editor.cut());
        criarBotao("compilar [F7]", "Compilar.png",
                KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), evt -> executarCompilacao());
        criarBotao("equipe [F1]", "Equipe.png",
                KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), evt -> mensagens.setText(MENSAGEM_EQUIPE));
    }

    private void criarBotao(String texto, String icone, KeyStroke atalho, ActionListener acao) {
        JButton botao = new JButton(texto);
        Image imagem = new ImageIcon(getClass().getResource("/interfacecompilador/" + icone)).getImage();
        botao.setIcon(new ImageIcon(imagem.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        botao.setVerticalTextPosition(SwingConstants.BOTTOM);
        botao.setHorizontalTextPosition(SwingConstants.CENTER);
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setPreferredSize(TAMANHO_BOTAO);
        botao.setMinimumSize(TAMANHO_BOTAO);
        botao.setMaximumSize(TAMANHO_BOTAO);
        // mantém o foco no editor, para que copiar/colar/recortar atuem sobre o texto editado
        botao.setFocusable(false);
        botao.addActionListener(acao);
        barraFerramentas.add(botao);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(atalho, texto);
        getRootPane().getActionMap().put(texto, new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                botao.doClick();
            }
        });
    }

    private void montarEditor() {
        editor.setBorder(BorderFactory.createEmptyBorder());
    }

    private void montarAreaMensagens() {
        mensagens.setEditable(false);
        mensagens.setBorder(BorderFactory.createEmptyBorder());
    }

    private void montarBarraStatus() {
        barraStatus.setPreferredSize(new Dimension(0, 25));
        barraStatus.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        mensagens.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
    }

    // ------------------------------------------------------------------ ações dos botões

    private void novo() {
        editor.setText("");
        mensagens.setText("");
        arquivo = null;
        barraStatus.setText("");
    }

    private void abrir() {
        JFileChooser seletor = criarSeletorDeArquivos();
        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // nenhum arquivo selecionado: mantém o estado anterior da interface
        }
        File selecionado = seletor.getSelectedFile();
        try {
            String texto = new String(Files.readAllBytes(selecionado.toPath()), StandardCharsets.UTF_8);
            editor.setText(texto.replace("\r\n", "\n"));
            editor.setCaretPosition(0);
            arquivo = selecionado;
            mensagens.setText("");
            atualizarBarraStatus();
        } catch (IOException ex) {
            mensagens.setText("erro ao abrir o arquivo: " + ex.getMessage());
        }
    }

    private void salvar() {
        File destino = arquivo;
        boolean arquivoNovo = destino == null;
        if (arquivoNovo) {
            JFileChooser seletor = criarSeletorDeArquivos();
            if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            destino = seletor.getSelectedFile();
            if (!destino.getName().toLowerCase().endsWith(".txt")) {
                destino = new File(destino.getAbsolutePath() + ".txt");
            }
        }
        try {
            String texto = editor.getText().replace("\n", System.lineSeparator());
            Files.write(destino.toPath(), texto.getBytes(StandardCharsets.UTF_8));
            arquivo = destino;
            mensagens.setText("");
            if (arquivoNovo) {
                atualizarBarraStatus();
            }
        } catch (IOException ex) {
            mensagens.setText("erro ao salvar o arquivo: " + ex.getMessage());
        }
    }

    private JFileChooser criarSeletorDeArquivos() {
        JFileChooser seletor = new JFileChooser();
        seletor.setFileFilter(new FileNameExtensionFilter("Arquivos texto (*.txt)", "txt"));
        seletor.setAcceptAllFileFilterUsed(false);
        return seletor;
    }

    private void atualizarBarraStatus() {
        barraStatus.setText(arquivo.getAbsolutePath());
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Interface().setVisible(true));
    }

    //Realiza afunilação das saídas, ou seja, independente do identificador, palavra reservada ou símbolo especial, retorna apenas sua classe, e não seu tipo exato.
    private String getClassePorExtenso(int idToken) {
        switch (idToken) {
            case Constants.t_cd_int:
            case Constants.t_cd_float:
            case Constants.t_cd_string:
            case Constants.t_cd_bool:
                return "identificador";

            case Constants.t_cte_int:
                return "constante_int";
            case Constants.t_cte_float:
                return "constante_float";
            case Constants.t_cte_string:
                return "constante_string";

            case Constants.t_and:
            case Constants.t_false:
            case Constants.t_if:
            case Constants.t_in:
            case Constants.t_isfalsedo:
            case Constants.t_istruedo:
            case Constants.t_module:
            case Constants.t_not:
            case Constants.t_or:
            case Constants.t_out:
            case Constants.t_true:
            case Constants.t_while:
                return "palavra reservada";

            case Constants.t_TOKEN_22:
            case Constants.t_TOKEN_23:
            case Constants.t_TOKEN_24:
            case Constants.t_TOKEN_25:
            case Constants.t_TOKEN_26:
            case Constants.t_TOKEN_27:
            case Constants.t_TOKEN_28:
            case Constants.t_TOKEN_29:
            case Constants.t_TOKEN_30:
            case Constants.t_TOKEN_31:
            case Constants.t_TOKEN_32:
            case Constants.t_TOKEN_33:
            case Constants.t_TOKEN_34:
            case Constants.t_TOKEN_35:
            case Constants.t_TOKEN_36:
            case Constants.t_TOKEN_37:
            case Constants.t_TOKEN_38:
            case Constants.t_TOKEN_39:
                return "símbolo especial";
        }
        return "";
    }


    private int getLinha(String texto, int posicao) {
        int linha = 1;
        for (int i = 0; i < posicao && i < texto.length(); i++) {
            if (texto.charAt(i) == '\n') {
                linha++;
            }
        }
        return linha;
    }

    private String extrairPalavra(String codigo, int pos) {
        int fim = pos;
        while (fim < codigo.length() && (Character.isLetterOrDigit(codigo.charAt(fim)) || codigo.charAt(fim) == '_')) {
            fim++;
        }
        return codigo.substring(pos, fim);
    }

    private void executarCompilacao() {
        mensagens.setText("");

        String codigoFonte = editor.getText();
        Lexico lexico = new Lexico();
        lexico.setInput(codigoFonte);

        try {

            //Formatação de texto conforme a saída
            Token token = null;
            StringBuilder saida = new StringBuilder();

            saida.append(String.format("%-10s %-25s %s\n", "linha", "classe", "lexema"));

            while ((token = lexico.nextToken()) != null) {
                int linha = getLinha(codigoFonte, token.getPosition());
                int pos = token.getPosition();

                String restante = codigoFonte.substring(pos);

                if (restante.startsWith("i_") || restante.startsWith("f_") || restante.startsWith("s_") || restante.startsWith("b_")) {
                    if (token.getId() != Constants.t_cd_int && token.getId() != Constants.t_cd_float && token.getId() != Constants.t_cd_string && token.getId() != Constants.t_cd_bool) {
                        mensagens.setText("linha " + linha + ": identificador inválido\n");
                        return;
                    }
                }

                if (token.getId() == Constants.t_palavra) {
                    String palavraInvalida = extrairPalavra(codigoFonte, pos);
                    mensagens.setText("linha " + linha + ": " + palavraInvalida + " palavra reservada inválida\n");
                    return;
                }

                String classe = getClassePorExtenso(token.getId());
                String lexema = token.getLexeme();

                saida.append(String.format("%-10d %-25s %s\n", linha, classe, lexema));
            }

            saida.append("\n");
            saida.append("programa compilado com sucesso\n");
            mensagens.setText(saida.toString());

        } catch (LexicalError e) {

            //Tratativas de erros apontados pelo GALS

            int linhaErro = getLinha(codigoFonte, e.getPosition());
            String msgErro = e.getMessage();
            int pos = e.getPosition();

            String restante = (pos < codigoFonte.length()) ? codigoFonte.substring(pos) : "";

            if (restante.startsWith("i_") || restante.startsWith("f_") || restante.startsWith("s_") || restante.startsWith("b_")) {
                mensagens.setText("linha " + linhaErro + ": identificador inválido\n");
            }

            else if (msgErro != null && (msgErro.contains("comentário") || msgErro.contains("ignorar") || msgErro.contains("<ignorar>"))) {
                mensagens.setText("linha " + linhaErro + ": comentário inválido ou não finalizado\n");
            }

            else if (restante.startsWith("\"") || (msgErro != null && msgErro.contains("constante_string"))) {
                mensagens.setText("linha " + linhaErro + ": constante_string inválida\n");
            }

            else if (msgErro != null && msgErro.contains("identificador")) {
                mensagens.setText("linha " + linhaErro + ": identificador inválido\n");
            }

            else if (restante.length() > 0 && Character.isLetter(restante.charAt(0))) {
                String palavraInvalida = extrairPalavra(codigoFonte, pos);
                mensagens.setText("linha " + linhaErro + ": " + palavraInvalida + " palavra reservada inválida\n");
            }

            else {
                char simbolo = (pos < codigoFonte.length()) ? codigoFonte.charAt(pos) : ' ';
                mensagens.setText("linha " + linhaErro + ": " + simbolo + " símbolo inválido\n");
            }
        }
    }

    /**
     * Numeração das linhas do editor, apresentada à esquerda e iniciando em 1.
     * Por ser um componente à parte do editor, seu conteúdo não pode ser alterado.
     */
    class NumeroDeLinhas extends JComponent {

        private static final long serialVersionUID = 1L;

        private final JTextArea editor;

        NumeroDeLinhas(JTextArea editor) {
            this.editor = editor;
            setFont(editor.getFont());
            setBackground(new Color(240, 240, 240));
            setOpaque(true);

            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent evt) {
                    atualizar();
                }

                @Override
                public void removeUpdate(DocumentEvent evt) {
                    atualizar();
                }

                @Override
                public void changedUpdate(DocumentEvent evt) {
                    atualizar();
                }
            });
            editor.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent evt) {
                    atualizar();
                }
            });
        }

        private void atualizar() {
            revalidate();
            repaint();
        }

        /**
         * Quantidade de linhas numeradas: preenche toda a altura visível do editor.
         */
        private int quantidadeDeLinhas(FontMetrics metrica) {
            return Math.max(1, editor.getHeight() / metrica.getHeight());
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics metrica = getFontMetrics(getFont());
            int largura = metrica.stringWidth(String.valueOf(quantidadeDeLinhas(metrica))) + 10;
            return new Dimension(largura, editor.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setFont(getFont());
            g.setColor(Color.GRAY);

            FontMetrics metrica = g.getFontMetrics();
            int alturaLinha = metrica.getHeight();
            int topo = editor.getInsets().top;
            int linhas = quantidadeDeLinhas(metrica);
            for (int i = 0; i < linhas; i++) {
                String numero = String.valueOf(i + 1);
                g.drawString(numero, getWidth() - metrica.stringWidth(numero) - 4,
                        topo + metrica.getAscent() + i * alturaLinha);
            }
        }
    }
}
