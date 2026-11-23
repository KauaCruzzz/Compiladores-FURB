package interfacecompilador;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Semantico implements Constants {
    
    private StringBuilder codigoObjeto = new StringBuilder();

    // Armazena os tipos das expressões durante sua avaliação.
    private Stack<String> pilhaTipos = new Stack<>();

    // Pilha destinada ao armazenamento de rótulos utilizados
    // no controle do fluxo de execução.
    private Stack<String> pilhaRotulos = new Stack<>();

    // Pilhas que armazenam os contextos das estruturas
    // condicionais (if) e de repetição (while).
    private Stack<SelecaoContexto> pilhaSelecoes = new Stack<>();
    private Stack<RepeticaoContexto> pilhaRepeticoes = new Stack<>();

    // Contador utilizado para gerar rótulos únicos no código IL.
    private int contadorRotulos = 0;

    // contexto das estruturas de condição (IF)

    // Armazena os rótulos necessários para controlar a execução
    // de uma estrutura condicional, incluindo possíveis alternativas.
    private static class SelecaoContexto {

        // Rótulo para o bloco alternativo (isFalseDo ou isTrueDo).
        String rotuloAlternativo;

        // Rótulo que indica o final da estrutura condicional.
        String rotuloFim;

        // Indica se a estrutura possui um bloco alternativo.
        boolean possuiAlternativa;

        SelecaoContexto(String rotuloAlternativo, String rotuloFim) {
            this.rotuloAlternativo = rotuloAlternativo;
            this.rotuloFim = rotuloFim;
            this.possuiAlternativa = false;
        }
    }

    // contexto das estruturas de repetição (WHILE)

    // Armazena os rótulos necessários para controlar o início e o
    //término de uma estrutura de repetição.
    private static class RepeticaoContexto {

        // Rótulo para o início do laço.
        String rotuloInicio;

        // Rótulo para a saída do laço.
        String rotuloFim;

        RepeticaoContexto(String rotuloInicio, String rotuloFim) {
            this.rotuloInicio = rotuloInicio;
            this.rotuloFim = rotuloFim;
        }
    }
   
   //Geração de rótulos do código IL
   // Cria rótulos únicos para desvios condicionais e repetições.
    private String novoRotulo() {
        return "IL_" + (contadorRotulos++);
    }

   //variáveis criadas de acordo com o pdf esquema
   private String operadorRelacional = "";
   private String identificador = "";
   private Map<String, Integer> tabelaSimbolos = new HashMap<>();

   public void executeAction(int action, Token token) throws SemanticError {
      System.out.println("Acao #"+action+", Token: "+token);

      switch (action) {
         case 101: acao101(); break;
         case 102: acao102(); break;
         case 103: acao103(token); break;
         case 104: acao104(); break;
         case 106: acao106(); break;
         case 108: acao108(); break;
         case 109: acao109(); break;
         case 110: acao110(token); break;
         case 111: acao111(); break;
         case 112: acao112(); break;
         case 113: acao113(); break;
         case 114: acao114(token); break;
         case 115: acao115(); break;
         case 116: acao116(token); break;
         case 117: acao117(); break;
         case 118: acao118(); break;
         case 119: acao119(); break;
         case 120: acao120(); break;
         case 121: acao121(token); break;
         case 122: acao122(); break;
         case 123: acao123(); break;
         case 124: acao124(); break;
         case 125: acao125(); break;
         case 126: acao126(); break;
         case 127: acao127(token); break;
         case 128: acao128(token); break;
         case 129: acao129(token); break;
         case 130: acao130(token); break;
         case 131: acao131(); break;
         case 132: acao132(); break;
         default:  throw new SemanticError("Acao semantica nao implementada: " + action);
      }
   }

   private void acao101() {
      codigoObjeto.append(".assembly extern mscorlib {}\n");
      codigoObjeto.append(".assembly _codigo_objeto{}\n");
      codigoObjeto.append(".module _codigo_objeto.exe\n");
      codigoObjeto.append("\n");
      codigoObjeto.append(".class public _codigo_objeto{\n");
      codigoObjeto.append(".method static public void _principal(){\n");
      codigoObjeto.append(".entrypoint\n");
}

   private void acao102() {
      codigoObjeto.append("ret\n");
      codigoObjeto.append("}\n");
      codigoObjeto.append("}");
   }

   public void acao103(Token token) {
      this.identificador = token.getLexeme();
   }

   public void acao104() {
      tabelaSimbolos.put(identificador, 0);

      String tipo = "";
      if (identificador.startsWith("cd_i")) {
         tipo = "int64";
      } else if (identificador.startsWith("cd_f")) {
         tipo = "float64";
      } else if (identificador.startsWith("cd_s")) {
         tipo = "string";
      } else if (identificador.startsWith("cd_")) {
         tipo = "bool";
      }

      codigoObjeto.append(".locals (" + tipo + " " + identificador + ")\n");
   }

   public void acao106() {
      String tipo = pilhaTipos.pop();
      if ("int64".equals(tipo)) {
         codigoObjeto.append("conv.i8\n");
      }
      codigoObjeto.append("stloc " + identificador + "\n");
   }

   public void acao108() {
      codigoObjeto.append("call string [mscorlib]System.Console::ReadLine()\n");

      if (identificador.startsWith("cd_i")) {
         codigoObjeto.append("call int64 [mscorlib]System.Int64::Parse(string)\n");
      } else if (identificador.startsWith("cd_f")) {
         codigoObjeto.append("call float64 [mscorlib]System.Double::Parse(string)\n");
      } else if (identificador.startsWith("cd_b")) {
         codigoObjeto.append("call bool [mscorlib]System.Boolean::Parse(string)\n");
      }

      codigoObjeto.append("stloc " + identificador + "\n");
   }

   private void acao109() {
      String tipo = pilhaTipos.pop();
      if ("int64".equals(tipo)) {
         codigoObjeto.append("conv.i8\n");
      }
      codigoObjeto.append("call void [mscorlib]System.Console::Write(" + tipo + ")\n");
    }

   private void acao110(Token token) {
      pilhaTipos.pop();

      String rotuloAlternativo = novoRotulo();
      String rotuloFim = novoRotulo();

      SelecaoContexto contexto = new SelecaoContexto(rotuloAlternativo, rotuloFim);

      pilhaSelecoes.push(contexto);

      if ("isTrueDo".equals(token.getLexeme())) {
         codigoObjeto.append("brfalse ")
            .append(rotuloAlternativo).append("\n");
      } else {
         codigoObjeto.append("brtrue ")
            .append(rotuloAlternativo).append("\n");
      }
}

   private void acao111() {
      SelecaoContexto contexto = pilhaSelecoes.pop();

      if (!contexto.possuiAlternativa) {
         codigoObjeto.append(contexto.rotuloAlternativo)
         .append(":\n");
      }

      codigoObjeto.append(contexto.rotuloFim)
      .append(":\n");
   }

   private void acao112() {
      SelecaoContexto contexto = pilhaSelecoes.peek();

      codigoObjeto.append("br ")
            .append(contexto.rotuloFim).append("\n");

      codigoObjeto.append(contexto.rotuloAlternativo)
            .append(":\n");

      contexto.possuiAlternativa = true;
   }

   private void acao113() {
      String rotuloInicio = novoRotulo();
      String rotuloFim = novoRotulo();

      RepeticaoContexto contexto =
         new RepeticaoContexto(rotuloInicio, rotuloFim);

      pilhaRepeticoes.push(contexto);

      codigoObjeto.append(rotuloInicio).append(":\n");
   }

   private void acao114(Token token) {
      String tipo = pilhaTipos.pop();

      RepeticaoContexto contexto = pilhaRepeticoes.peek();

      if ("isTrueDo".equals(token.getLexeme())) {
         codigoObjeto.append("brfalse ")
         .append(contexto.rotuloFim).append("\n");
      } else {
         codigoObjeto.append("brtrue ")
            .append(contexto.rotuloFim).append("\n");
      }
   }

   private void acao115() {
      RepeticaoContexto contexto = pilhaRepeticoes.pop();

      codigoObjeto.append("br ")
      .append(contexto.rotuloInicio).append("\n");

      codigoObjeto.append(contexto.rotuloFim)
      .append(":\n");
   }

   private void acao116(Token token) throws SemanticError {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      if ("bool".equals(tipo1) && "bool".equals(tipo2)) {
         pilhaTipos.push("bool");
         codigoObjeto.append("and\n");
      } else {
         throw new SemanticError("tipos incompatíveis em operador lógico and", token.getPosition());
      }  
   }

   public void acao117 () {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      pilhaTipos.push("bool");
      codigoObjeto.append("or\n");
   }

   private void acao118() {
      pilhaTipos.push("bool");
      codigoObjeto.append("ldc.i4.1\n");
   }

   private void acao119() {
      pilhaTipos.push("bool");
      codigoObjeto.append("ldc.i4.0\n");
   }

   private void acao120() {
      String tipo = pilhaTipos.pop();
      pilhaTipos.push("bool");
      codigoObjeto.append("ldc.i4.1\n");
      codigoObjeto.append("xor\n");
   }

   public void acao121(Token token) {
      operadorRelacional = token.getLexeme();
   }

   public void acao122() {
      pilhaTipos.pop();
      pilhaTipos.pop();
      pilhaTipos.push("bool");

      switch (operadorRelacional) {
         case "=":
            codigoObjeto.append("ceq\n");
            break;

         case "<":
            codigoObjeto.append("clt\n");
            break;

         case ">":   
            codigoObjeto.append("cgt\n");
            break;

         case "<>":
            codigoObjeto.append("ceq\n");
            codigoObjeto.append("ldc.i4.1\n");
            codigoObjeto.append("xor\n");
            break;
   }
}

   private void acao123() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      if ("int64".equals(tipo1) && "int64".equals(tipo2)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }
      codigoObjeto.append("add\n");
   }

   private void acao124() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      if ("int64".equals(tipo1) && "int64".equals(tipo2)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }
      codigoObjeto.append("sub\n");
   }

   private void acao125() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      if ("int64".equals(tipo1) && "int64".equals(tipo2)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }
      codigoObjeto.append("mul\n");
   }

   private void acao126() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      pilhaTipos.push("float64");
      codigoObjeto.append("div\n");
   }

   public void acao127(Token token) {
      String id = token.getLexeme();

      if (id.startsWith("cd_i")) {
         pilhaTipos.push("int64");
      } else if (id.startsWith("cd_f")) {
         pilhaTipos.push("float64");
      } else if (id.startsWith("cd_s")) {
         pilhaTipos.push("string");
      } else if (id.startsWith("cd_b")) {
         pilhaTipos.push("bool");
      }

      codigoObjeto.append("ldloc " + id + "\n");

      if (id.startsWith("cd_i")) {
         codigoObjeto.append("conv.r8\n");
      }
   }

   private void acao128(Token token) {
     pilhaTipos.push("int64");
     codigoObjeto.append("ldc.i8 " + token.getLexeme() + "\n");
     codigoObjeto.append("conv.r8\n");
   }

   private void acao129(Token token) {
      pilhaTipos.push("float64");
      codigoObjeto.append("ldc.r8 " + token.getLexeme() + "\n");
      //necessita de replace de . para , ?
   }

   public void acao130(Token token) {
      pilhaTipos.push("string");
      codigoObjeto.append("ldstr " + token.getLexeme() + "\n");
   }

   private void acao131() {
      String tipo = pilhaTipos.pop();
      if ("int64".equals(tipo)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }	  
   }     

   private void acao132() {
      String tipo = pilhaTipos.pop();
      if ("int64".equals(tipo)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }	
      codigoObjeto.append("ldc.i8 -1\n");
      codigoObjeto.append("conv.r8\n");
      codigoObjeto.append("mul\n");
   }
	
   public String getCodigoObjeto() {
      return codigoObjeto.toString();
   }
}