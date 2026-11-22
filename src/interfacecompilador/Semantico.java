package interfacecompilador;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Semantico implements Constants {
    
   //registros semanticos
   private StringBuilder  codigoObjeto = new StringBuilder();
   private Stack<String>  pilhaTipos = new Stack<>();

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
      if (identificador.startsWith("i_")) {
         tipo = "int64";
      } else if (identificador.startsWith("f_")) {
         tipo = "float64";
      } else if (identificador.startsWith("s_")) {
         tipo = "string";
      } else if (identificador.startsWith("b_")) {
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

      if (identificador.startsWith("i_")) {
         codigoObjeto.append("call int64 [mscorlib]System.Int64::Parse(string)\n");
      } else if (identificador.startsWith("f_")) {
         codigoObjeto.append("call float64 [mscorlib]System.Double::Parse(string)\n");
      } else if (identificador.startsWith("b_")) {
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

      if (id.startsWith("i_")) {
         pilhaTipos.push("int64");
      } else if (id.startsWith("f_")) {
         pilhaTipos.push("float64");
      } else if (id.startsWith("s_")) {
         pilhaTipos.push("string");
      } else if (id.startsWith("b_")) {
         pilhaTipos.push("bool");
      }

      codigoObjeto.append("ldloc " + id + "\n");

      if (id.startsWith("i_")) {
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