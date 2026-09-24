import java.util.Scanner;

public class Interpretador {

    private final Scanner scanner = new Scanner(System.in);

    public void executarInstrucao(Processo processo) {

        if (processo.getEstado() != EstadoProcesso.EXECUTANDO) {
            return;
        }

        Instrucao instrucao = processo.getInstrucaoAtual();

        String comando = instrucao.getComando().toUpperCase();
        String operando = instrucao.getOperando();

        switch (comando) {

            case "LOAD":
                processo.setAcc(obterValor(processo, operando));
                processo.setPc(processo.getPc() + 1);
                break;

            case "STORE":

                if (operando.startsWith("#")) {
                    throw new IllegalArgumentException(
                            "STORE precisa usar uma variavel."
                    );
                }

                processo.definirVariavel(
                        operando,
                        processo.getAcc()
                );

                processo.setPc(processo.getPc() + 1);
                break;

            case "ADD":

                processo.setAcc(
                        processo.getAcc()
                        + obterValor(processo, operando)
                );

                processo.setPc(processo.getPc() + 1);
                break;

            case "SUB":

                processo.setAcc(
                        processo.getAcc()
                        - obterValor(processo, operando)
                );

                processo.setPc(processo.getPc() + 1);
                break;

            case "MULT":

                processo.setAcc(
                        processo.getAcc()
                        * obterValor(processo, operando)
                );

                processo.setPc(processo.getPc() + 1);
                break;

            case "DIV": {

                int divisor =
                        obterValor(processo, operando);

                if (divisor == 0) {

                    throw new ArithmeticException(
                            "Nao e possível dividir por zero."
                    );
                }

                processo.setAcc(
                        processo.getAcc() / divisor
                );

                processo.setPc(processo.getPc() + 1);
                break;
            }

            case "BRANY":

                processo.setPc(
                        processo.getRotulo(operando)
                );

                break;

            case "BRPOS":

                if (processo.getAcc() > 0) {

                    processo.setPc(
                            processo.getRotulo(operando)
                    );

                } else {

                    processo.setPc(
                            processo.getPc() + 1
                    );
                }

                break;

            case "BRZERO":

                if (processo.getAcc() == 0) {

                    processo.setPc(
                            processo.getRotulo(operando)
                    );

                } else {

                    processo.setPc(
                            processo.getPc() + 1
                    );
                }

                break;

            case "BRNEG":

                if (processo.getAcc() < 0) {

                    processo.setPc(
                            processo.getRotulo(operando)
                    );

                } else {

                    processo.setPc(
                            processo.getPc() + 1
                    );
                }

                break;

            case "SYSCALL":

                executarSyscall(
                        processo,
                        operando
                );

                break;

            default:

                throw new IllegalArgumentException(
                        "Comando desconhecido: "
                        + comando
                );
        }
    }

    private int obterValor(
            Processo processo,
            String operando
    ) {

        if (operando.startsWith("#")) {

            return Integer.parseInt(
                    operando.substring(1)
            );
        }

        return processo.getVariavel(operando);
    }

    private void executarSyscall(
            Processo processo,
            String operando
    ) {

        int indice =
                Integer.parseInt(operando);

        processo.setPc(
                processo.getPc() + 1
        );

        if (indice == 0) {

            processo.setEstado(
                    EstadoProcesso.FINALIZADO
            );

            System.out.println(
                    "[" + processo.getNome()
                    + "] Finalizado (SYSCALL 0)"
            );

        } else if (indice == 1) {

            System.out.println(
                    "[" + processo.getNome()
                    + "] Impressao (SYSCALL 1): "
                    + processo.getAcc()
            );

            processo.setEstado(
                    EstadoProcesso.BLOQUEADO
            );

            processo.setTempoBloqueado(3);

        } else if (indice == 2) {

            System.out.print(
                    "[" + processo.getNome()
                    + "] Digite um valor inteiro: "
            );

            int valor = scanner.nextInt();

            processo.setAcc(valor);

            processo.setEstado(
                    EstadoProcesso.BLOQUEADO
            );

            processo.setTempoBloqueado(3);

        } else {

            throw new IllegalArgumentException(
                    "SYSCALL desconhecida: "
                    + indice
            );
        }
    }
}