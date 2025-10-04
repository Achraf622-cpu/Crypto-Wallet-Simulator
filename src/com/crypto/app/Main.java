package com.crypto.app;

import com.crypto.app.domain.*;
import com.crypto.app.repository.InMemoryTransactionRepository;
import com.crypto.app.repository.InMemoryWalletRepository;
import com.crypto.app.repository.TransactionRepository;
import com.crypto.app.db.Database;
import com.crypto.app.service.MempoolService;
import com.crypto.app.service.TransactionService;
import com.crypto.app.service.WalletService;
import com.crypto.app.service.DbPersistenceService;
import com.crypto.app.util.ConsoleTable;
import com.crypto.app.util.InputValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static DbPersistenceService dbSvc;

    public static void main(String[] args) {
        InMemoryWalletRepository walletRepo = new InMemoryWalletRepository();
        TransactionRepository txRepo = new InMemoryTransactionRepository();
        WalletService walletService = new WalletService(walletRepo);
        TransactionService transactionService = new TransactionService(txRepo, walletRepo);
        MempoolService mempoolService = new MempoolService(txRepo);

        // Always enable JDBC persistence
        DbPersistenceService dbService = null;
        Database.getInstance().connectFromEnvOrDefault();
        dbService = new DbPersistenceService();
        dbSvc = dbService;
        System.out.println("[INFO] JDBC persistence enabled (set JDBC_URL/JDBC_USER/JDBC_PASSWORD/JDBC_DRIVER as needed)");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    createWalletFlow(walletService, dbService);
                    break;
                case "2":
                    createTransactionFlow(walletService, transactionService, mempoolService, dbService);
                    break;
                case "3":
                    viewPositionFlow(mempoolService, transactionService);
                    break;
                case "4":
                    compareFeesFlow(mempoolService, transactionService);
                    break;
                case "5":
                    viewMempoolFlow(mempoolService);
                    break;
                case "6":
                    listWalletsDbFlow();
                    break;
                case "7":
                    listTransactionsDbFlow();
                    break;
                case "8":
                    depositFlow();
                    break;
                case "9":
                    advanceTimeFlow(mempoolService);
                    break;
                case "10":
                    clockAndMempoolStateFlow(mempoolService);
                    break;
                case "0":
                    running = false;
                    System.out.println("Au revoir!");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("==== Wallet Crypto (Java 8) ====");
        System.out.println("1) Creer un wallet crypto");
        System.out.println("2) Creer une nouvelle transaction");
        System.out.println("3) Calculer la position dans le mempool et temps estime");
        System.out.println("4) Comparer les 3 niveaux de frais");
        System.out.println("5) Consulter l'etat actuel du mempool");
        System.out.println("6) Lister mes wallets");
        System.out.println("7) Lister mes transactions par wallet");
        System.out.println("8) Deposer des fonds sur un wallet");
        System.out.println("9) Avancer le temps (minutes) et traiter les confirmations");
        System.out.println("10) Voir l'horloge/etat du mempool (deterministe)");
        System.out.println("0) Quitter");
        System.out.print("Votre choix: ");
    }

    private static void createWalletFlow(WalletService walletService, DbPersistenceService dbService) {
        System.out.println("Type de crypto (1=BITCOIN, 2=ETHEREUM): ");
        String t = SCANNER.nextLine().trim();
        CryptoType type = "1".equals(t) ? CryptoType.BITCOIN : CryptoType.ETHEREUM;
        Wallet wallet = walletService.createWallet(type);
        if (dbService != null) {
            dbService.saveWallet(wallet);
        }
        System.out.println("Wallet cree: " + wallet);
    }

    private static void createTransactionFlow(WalletService walletService, TransactionService txService, MempoolService mempoolService, DbPersistenceService dbService) {
        System.out.print("ID wallet source: ");
        String walletId = SCANNER.nextLine().trim();
        Wallet wallet = walletService.findById(walletId);
        if (wallet == null) {
            System.out.println("Wallet introuvable.");
            return;
        }

        System.out.print("Adresse destination: ");
        String to = SCANNER.nextLine().trim();
        if (!InputValidator.isValidAddress(to, wallet.getType())) {
            System.out.println("Adresse invalide pour " + wallet.getType());
            return;
        }

        System.out.print("Montant (nombre positif): ");
        String amountStr = SCANNER.nextLine().trim();
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Montant doit etre > 0.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Montant invalide.");
            return;
        }

        System.out.println("Priorite (1=ECONOMIQUE, 2=STANDARD, 3=RAPIDE): ");
        String p = SCANNER.nextLine().trim();
        Priority priority = "3".equals(p) ? Priority.RAPIDE : ("2".equals(p) ? Priority.STANDARD : Priority.ECONOMIQUE);

        Transaction tx = txService.createTransaction(wallet, wallet.getAddress(), to, amount, priority);
        if (dbService != null) {
            dbService.saveTransaction(tx, wallet.getId());
            // Debiter le wallet: montant + fees
            java.math.BigDecimal totalDebit = amount.add(tx.getFee());
            dbService.updateWalletBalance(wallet.getId(), totalDebit.negate());
        }
        // Enqueue dans le mempool deterministe
        mempoolService.enqueue(tx);
        System.out.println("Transaction creee: " + tx.getId());
        System.out.println(tx);
    }

    private static void viewPositionFlow(MempoolService mempoolService, TransactionService txService) {
        System.out.print("ID transaction: ");
        String id = SCANNER.nextLine().trim();
        Transaction tx = txService.findById(id);
        if (tx == null) {
            System.out.println("Transaction introuvable.");
            return;
        }
        mempoolService.regenerateRandomMempool(15, tx);
        int position = mempoolService.calculatePosition(tx);
        int total = mempoolService.getPendingCount();
        long minutes = position * 10L;
        System.out.println("Votre transaction est en position " + position + " sur " + total + ". ~" + minutes + " min");
    }

    private static void compareFeesFlow(MempoolService mempoolService, TransactionService txService) {
        System.out.print("ID wallet: ");
        String walletId = SCANNER.nextLine().trim();
        Wallet wallet = txService.getWalletRepository().findById(walletId);
        if (wallet == null) {
            System.out.println("Wallet introuvable.");
            return;
        }
        // Ne demande plus l'adresse destinataire pour la comparaison
        String to = wallet.getType() == CryptoType.BITCOIN ? "1DummyDestXXXXXXXXXXXXXXXXXXXXXXXX" : "0xaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        System.out.print("Montant: ");
        String a = SCANNER.nextLine().trim();
        BigDecimal amount;
        try {
            amount = new BigDecimal(a);
        } catch (Exception e) {
            System.out.println("Montant invalide.");
            return;
        }

        // Prepare three hypothetical transactions
        Transaction econ = txService.createDraftTransaction(wallet, wallet.getAddress(), to, amount, Priority.ECONOMIQUE);
        Transaction std = txService.createDraftTransaction(wallet, wallet.getAddress(), to, amount, Priority.STANDARD);
        Transaction fast = txService.createDraftTransaction(wallet, wallet.getAddress(), to, amount, Priority.RAPIDE);

        mempoolService.regenerateRandomMempool(15, null);

        int posE = mempoolService.calculatePosition(econ);
        int posS = mempoolService.calculatePosition(std);
        int posF = mempoolService.calculatePosition(fast);
        int total = mempoolService.getPendingCount();

        ConsoleTable table = new ConsoleTable("Priorite", "Fees", "Position", "Temps estime");
        table.addRow("ECONOMIQUE", econ.getFee().toPlainString(), String.valueOf(posE) + "/" + total, (posE * 10) + " min");
        table.addRow("STANDARD", std.getFee().toPlainString(), String.valueOf(posS) + "/" + total, (posS * 10) + " min");
        table.addRow("RAPIDE", fast.getFee().toPlainString(), String.valueOf(posF) + "/" + total, (posF * 10) + " min");
        System.out.println(table.render());
    }

    private static void viewMempoolFlow(MempoolService mempoolService) {
        // plus de generation aleatoire
        List<Transaction> list = mempoolService.getPendingTransactionsOrdered();
        ConsoleTable table = new ConsoleTable("ID", "Type", "Priority", "Fees");
        for (Transaction t : list) {
            table.addRow(shorten(t.getId()), t.getType().name(), t.getPriority().name(), t.getFee().toPlainString());
        }
        System.out.println(table.render());
    }

    private static void listWalletsDbFlow() {
        if (dbSvc == null) {
            System.out.println("Base de donnees non connectee.");
            return;
        }
        List<String[]> rows = dbSvc.listWallets();
        ConsoleTable t = new ConsoleTable("ID", "Type", "Adresse", "Solde");
        for (String[] r : rows) t.addRow(r);
        System.out.println(t.render());
    }

    private static void listTransactionsDbFlow() {
        if (dbSvc == null) {
            System.out.println("Base de donnees non connectee.");
            return;
        }
        System.out.print("Wallet ID: ");
        String walletId = SCANNER.nextLine().trim();
        List<String[]> rows = dbSvc.listTransactionsByWallet(walletId);
        ConsoleTable t = new ConsoleTable("ID", "Type", "From", "To", "Amount", "Priority", "Fee", "Status", "Created");
        for (String[] r : rows) t.addRow(r);
        System.out.println(t.render());
    }

    private static void depositFlow() {
        if (dbSvc == null) {
            System.out.println("Base de donnees non connectee.");
            return;
        }
        System.out.print("Wallet ID: ");
        String walletId = SCANNER.nextLine().trim();
        System.out.print("Montant a deposer: ");
        String amt = SCANNER.nextLine().trim();
        try {
            java.math.BigDecimal delta = new java.math.BigDecimal(amt);
            if (delta.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                System.out.println("Montant doit etre > 0.");
                return;
            }
            dbSvc.updateWalletBalance(walletId, delta);
            System.out.println("Depot effectue.");
        } catch (Exception e) {
            System.out.println("Montant invalide.");
        }
    }

    private static void advanceTimeFlow(MempoolService mempoolService) {
        if (dbSvc == null) {
            System.out.println("Base de donnees non connectee.");
            return;
        }
        System.out.print("Minutes a avancer: ");
        String m = SCANNER.nextLine().trim();
        try {
            long minutes = Long.parseLong(m);
            mempoolService.advanceAndConfirm(minutes, dbSvc);
            System.out.println("Temps avance. Minutes courantes: " + mempoolService.getCurrentMinutes());
        } catch (Exception e) {
            System.out.println("Valeur invalide.");
        }
    }

    private static void clockAndMempoolStateFlow(MempoolService mempoolService) {
        System.out.println("Horloge (minutes): " + mempoolService.getCurrentMinutes());
        List<Transaction> list = mempoolService.getPendingTransactionsOrdered();
        ConsoleTable table = new ConsoleTable("ID", "Fees", "Priority", "Remaining(min)");
        for (Transaction t : list) {
            Long rem = mempoolService.getRemainingMinutes(t.getId());
            table.addRow(shorten(t.getId()), t.getFee().toPlainString(), t.getPriority().name(), rem == null ? "-" : String.valueOf(rem));
        }
        System.out.println(table.render());
    }

    private static String shorten(String id) {
        if (id == null) return "";
        if (id.length() <= 8) return id;
        return id.substring(0, 8) + "...";
    }
}


