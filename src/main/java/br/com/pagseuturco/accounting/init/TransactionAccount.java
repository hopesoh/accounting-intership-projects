package br.com.pagseuturco.accounting.init;

import br.com.pagseuturco.accounting.data.FinancialTurnoverFactory;
import br.com.pagseuturco.accounting.data.Turnover;
import br.com.pagseuturco.accounting.file.FileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class TransactionAccount {

    private static final String SEMICOLON = ";";
    private static final String BOOKLET_HEADER = "numero_documento; nome_emissor;identificacao_emissor;valor_documento;conta;data_recebimento";
    private static final String GENNERICCARD_HEADER = "hash_cartao;tipo_transacao;valor;conta;data_transacao";
    private static final String TRANSFER_HEADER = "tipo;valor;conta;data_transacao";
    private static final String HEADER = "tipo_movimentacao;valor_movimentacao;conta;data_transacao";

    public void processFile () throws IOException {

        FileReader fileReader = new FileReader();
        BufferedReader reader = fileReader.readFile();

        ArrayList<String> fileIntoList = transformFileIntoList(reader);
        String turnoverType = identifyTurnoverByType(fileIntoList.get(0));

        ArrayList<Turnover> turnoverList = transformIntoTurnoverList(turnoverType, reader, fileIntoList.get(0));

        reader.close();
    }

    public ArrayList<String> transformFileIntoList(BufferedReader reader) throws IOException {

        String fileLine = null;
        ArrayList<String> turnoverArrayList = new ArrayList<String>();


        while ((fileLine = reader.readLine()) != null) {
            turnoverArrayList.add(fileLine);
        }

        return turnoverArrayList;
    }

    public String identifyTurnoverByType(String header) {
        switch (header) {
            case BOOKLET_HEADER:
                return "BOOKLET";
            case GENNERICCARD_HEADER:
                return "GENNERICCARD";
            case TRANSFER_HEADER:
                return "TRANSFER";
            default:
                return null;
        }
    }

    public ArrayList<Turnover> transformIntoTurnoverList(String turnoverType, BufferedReader reader, String header) throws IOException {

        String fileLine = null;
        ArrayList<Turnover> turnoverArrayList = new ArrayList<Turnover>();

        while ((fileLine = reader.readLine()) != null) {

            if (fileLine.equals(header)) {
                continue;
            }

            String[] splittedLine = fileLine.split(SEMICOLON, -1);

            if (splittedLine.length == 0) {
                return null;
            }

            FinancialTurnoverFactory financialTurnoverFactory = new FinancialTurnoverFactory();
            Turnover financialTurnover = financialTurnoverFactory.build(turnoverType,splittedLine);
            turnoverArrayList.add(financialTurnover);
        }

        return turnoverArrayList;
    }
}