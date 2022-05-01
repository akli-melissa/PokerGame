package IHM;
import Client.Client;
import Client.States.MenuState;
import Game.Player;
import Game.PokerGame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.*;

public class ClientFrame extends JFrame {
    private  final int  MAX_PLAYERS = 10;
    private  JPanel         messagePanel;                   // Panel where messages are displayed
    private  JPanel         cashPanel;                      // Panel where info about the game and betting is displayed
    private  JTextField     messageText;                    // The message text being displayed
    private  JTextField     bankText;                       // How much money this player had
    private  JTextField     potText;                        // How much money is in the pot
    private  JTextField     betText;                        // How much is the current bet
    private  JTextField     yourBetText;                    // How much is this player about to bet
    private  ArrayList      positionLabels;                 // Labels under each possible player location
    private  ArrayList      cashLabels;                     // Labels showing how much money each player has.
    private  ArrayList      dealerLabels;                   // Labels with icon of dealer button to designate who is the dealer.
    private  String         playerText[];                   // String of the text under each player
    private  JButton        foldButton;                     // Button used to fold
    private  JButton        checkButton;                    // Button used to check
    private  JButton        callButton;                     // Button used to call the current bet
    private  JButton        raiseButton;                     // Button used to raise
    private  JPanel        table;
    private  JButton        startGame;
    private ArrayList<JComponent> startGamePanel = new ArrayList<JComponent>() ;
    private ArrayList<JComponent> joinPanel = new ArrayList<JComponent>() ;
    private ArrayList<JComponent> getListPanel = new ArrayList<JComponent>() ;
    private ArrayList<JComponent> roundPanel = new ArrayList<JComponent>() ;
    private ArrayList<JComponent> createPanel = new ArrayList<JComponent>() ;
    private ArrayList<JLabel> cardsLabels = new ArrayList<JLabel>();
    private ArrayList<JLabel> namesLabels = new ArrayList<JLabel>();
    private ArrayList<JLabel> cardsOnTable = new ArrayList<JLabel>();



    private JTextField join;
    private JButton okName;

    private MenuState       thisPlayer;
    private PokerGame game;
    private Client client;
    public ClientFrame(String title , Client client){
        super(title);
        this.client=client;
        playerText = new String[MAX_PLAYERS];

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout( new GridLayout( 3,1 ) );
        JPanel buttons = new JPanel();
        buttons.setLayout( new GridLayout( 1,4 ) );

        foldButton = new JButton( "Fold" );
        //foldButton.addActionListener( new foldAction() );
        foldButton.setFocusPainted( false );
        buttons.add( foldButton );
        checkButton = new JButton( "Check" );
        //checkButton.addActionListener( new checkAction() );
        checkButton.setFocusPainted( false );
        buttons.add( checkButton );
        callButton = new JButton( "Call" );
        //callButton.addActionListener( new callAction() );
        callButton.setFocusPainted( false );
        buttons.add( callButton );
        raiseButton = new JButton( "Raise" );
        //callButton.addActionListener( new callAction() );
        callButton.setFocusPainted( false );
        buttons.add( raiseButton );

        cashPanel = new JPanel();
        cashPanel.setLayout( new GridLayout( 1,4 ) );
        cashPanel.setBorder( new EtchedBorder( EtchedBorder.RAISED ) );

        bankText = new JTextField("Bank ");
        potText = new JTextField("Pot ");
        betText = new JTextField("Bet");

        bankText.setEditable( false );
        potText.setEditable( false );
        betText.setEditable( false );

        cashPanel.add( bankText );
        cashPanel.add( potText );
        cashPanel.add( betText );

        messagePanel = new JPanel();
        messagePanel.setLayout( new BorderLayout() );
        messageText = new JTextField("Send a message");
        messageText.setEditable( false );
        messagePanel.add( messageText );

        yourBetText = new JTextField("Your bet");
        yourBetText.setPreferredSize( new Dimension( 325,1 ) );
        yourBetText.setEditable( false );
        messagePanel.add( yourBetText, BorderLayout.EAST );

        bottomPanel.add( cashPanel );
        bottomPanel.add( messagePanel );
        bottomPanel.add( buttons );

        getContentPane().add( bottomPanel, BorderLayout.SOUTH );

        table = new JPanel( null);
        table.setBackground(new Color(0, 128, 0));

        startGame = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                setPanel(joinPanel);
            }
        });
        startGame.setText("Start the game");
        startGame.setBounds(610,230,150,30);
        startGamePanel.add(startGame);

        join = new JTextField();
        join.setBounds(610,230,150,30);
        joinPanel.add(join);

        okName = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                String messageToSend="100 HELLO PLAYER "+join.getText();
                messageToSend=messageToSend.trim();
                client.analyseMessageToSend(messageToSend);
                client.writeToServer(messageToSend);
                getList();
            }
        });
        okName.setText("OK");
        okName.setBounds(650,290,70,30);
        joinPanel.add(okName);

        JLabel typeLabel = new JLabel("Type : ");
        typeLabel.setBounds(100,50,150,30);
        JTextField typeText = new JTextField();
        typeText.setBounds(100,90,150,30);
        createPanel.add(typeLabel);
        createPanel.add(typeText);

        JLabel nbPlayerLabel = new JLabel("nombre maximal de joueur : ");
        JTextField nbPlayerText = new JTextField();
        nbPlayerLabel.setBounds(270,50,200,30);
        nbPlayerText.setBounds(270,90,200,30);
        createPanel.add(nbPlayerLabel);
        createPanel.add(nbPlayerText);

        JLabel minBetLabel = new JLabel("Enchère minimale : ");
        JTextField minBetText = new JTextField();
        minBetLabel.setBounds(490,50,150,30);
        minBetText.setBounds(490,90,150,30);
        createPanel.add(minBetLabel);
        createPanel.add(minBetText);

        JLabel stackLabel = new JLabel("Stack : ");
        JTextField stackText = new JTextField();
        stackLabel.setBounds(670,50,150,30);
        stackText.setBounds(670,90,150,30);
        createPanel.add(stackLabel);
        createPanel.add(stackText);

        JButton createRound = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                String messageToSend="110 CREATE "+typeText.getText()+" PLAYER "+nbPlayerText.getText()+" MIN "+minBetText.getText()+" STACK "+stackText.getText();
                messageToSend=messageToSend.trim();
                client.analyseMessageToSend(messageToSend);
                client.writeToServer(messageToSend);
                setPanel(roundPanel);
            }
        });
        createRound.setText("Créer la partie");
        createRound.setBounds(400,150,150,30);
        createPanel.add(createRound);

        JLabel dealer=new JLabel();
        dealer.setIcon(new ImageIcon(this.getClass().getResource("/images/button_present.png")));
        dealer.setBounds(170,342,50,50);
        roundPanel.add(dealer);

        for (int i=0;i<MAX_PLAYERS;i++) {
            String path;
            if(i==0){
                JLabel name = new JLabel();
                name.setBounds(75, 220, 90, 30);
                namesLabels.add(name);
                roundPanel.add(name);
                JLabel[] cards = new JLabel[5];
                for (int j = 0; j < 5; j++) {
                    cards[j] = new JLabel();
                    path = "/images/card_44.png";
                    cards[j].setIcon(new ImageIcon(this.getClass().getResource(path)));
                    cards[j].setBounds(50 + (j * 15), 255, 70, 100);
                    cardsLabels.add(cards[j]);
                }
                for (int j = 4; j >= 0; j--) {
                    roundPanel.add(cards[j]);
                }
            }
            else{
                if(i<5){
                    JLabel name = new JLabel();
                    name.setBounds(100+(i*200), 20, 90, 30);
                    namesLabels.add(name);
                    roundPanel.add(name);
                    JLabel[] cards = new JLabel[5];
                    for (int j = 0; j < 5; j++) {
                        cards[j] = new JLabel();
                        path = "/images/card_back.png";
                        cards[j].setIcon(new ImageIcon(this.getClass().getResource(path)));
                        cards[j].setBounds(80 + (j * 15) + (i*200), 55, 70, 100);
                        cardsLabels.add(cards[j]);
                    }
                    for (int j = 4; j >= 0; j--) {
                        roundPanel.add(cards[j]);
                    }
                }
                else{
                    if(i==5){
                        JLabel name = new JLabel();
                        name.setBounds(1120, 220, 90, 30);
                        roundPanel.add(name);
                        JLabel[] cards = new JLabel[5];
                        for (int j = 0; j < 5; j++) {
                            cards[j] = new JLabel();
                            path = "/images/card_back.png";
                            cards[j].setIcon(new ImageIcon(this.getClass().getResource(path)));
                            cards[j].setBounds(1100 + (j * 15), 255, 70, 100);
                            cardsLabels.add(cards[j]);
                        }
                        for (int j = 4; j >= 0; j--) {
                            roundPanel.add(cards[j]);
                        }
                    }
                    else{
                        JLabel name = new JLabel();
                        name.setBounds(905 -((i-6)*200), 415, 90, 30);
                        namesLabels.add(name);
                        roundPanel.add(name);
                        JLabel[] cards = new JLabel[5];
                        for (int j = 0; j < 5; j++) {
                            cards[j] = new JLabel();
                            path = "/images/card_back.png";
                            cards[j].setIcon(new ImageIcon(this.getClass().getResource(path)));
                            cards[j].setBounds(885 +(j * 15) -((i-6)*200), 450, 70, 100);
                            cardsLabels.add(cards[j]);
                        }
                        for (int j = 4; j >= 0; j--) {
                            roundPanel.add(cards[j]);
                        }
                    }
                }
            }
        }
        JLabel[] cards = new JLabel[5];
        for (int j = 0; j < 5; j++) {
            cards[j] = new JLabel();
            String path = "/images/card_placeholder.png";
            cards[j].setIcon(new ImageIcon(this.getClass().getResource(path)));
            cards[j].setBounds(310 + (j * 150) , 255, 75, 100);
            cardsOnTable.add(cards[j]);
        }
        for (int j = 4; j >= 0; j--) {
            roundPanel.add(cards[j]);
        }

        getContentPane().add( table, BorderLayout.CENTER );
        setPanel(roundPanel);
        setResizable( false );
        setBounds( 35,20,1300,700);
        setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
        setVisible( true );
    }
    private void setPanel(ArrayList<JComponent> panel){
        table.removeAll();
        table.setLayout(null);
        for(int i=0 ; i<panel.size(); i++) {
            table.add(panel.get(i));
        }
        repaint();
        validate();
    }
    private void getList(){
        JTextField id = new JTextField("Id");
        id.setEditable(false);
        id.setBounds(0,0,150,30);
        getListPanel.add(id);

        JTextField type = new JTextField("Type");
        type.setEditable(false);
        type.setBounds(150,0,150,30);
        getListPanel.add(type);

        JTextField nbPlayer = new JTextField("Nombre de joueurs");
        nbPlayer.setEditable(false);
        nbPlayer.setBounds(2*150,0,150,30);
        getListPanel.add(nbPlayer);

        JTextField minBet = new JTextField("Min bet");
        minBet.setEditable(false);
        minBet.setBounds(3*150,0,150,30);
        getListPanel.add(minBet);

        JTextField stack = new JTextField("stack");
        stack.setEditable(false);
        stack.setBounds(4*150,0,150,30);
        getListPanel.add(stack);

        String messageToSend="120 GETLIST";
        messageToSend=messageToSend.trim();
        client.analyseMessageToSend(messageToSend);
        client.writeToServer(messageToSend);

        setPanel(getListPanel);
        int nbRooms = ((MenuState)client.getGameState()).getN();
        /*
        while(nbRooms!=0 &&(((MenuState)client.getGameState()).getGamesList()==null || ((MenuState)client.getGameState()).getGamesList(nbRooms-1)==null)){
            System.out.println(nbRooms!=0 &&(((MenuState)client.getGameState()).getGamesList()==null || ((MenuState)client.getGameState()).getGamesList(nbRooms-1)==null));

        }
        */
        for(int i=0 ; i<nbRooms ; i++){
            JTextField id1 = new JTextField();
            id1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getId());
            id1.setBounds(0,30*(i+1),150,30);
            id1.setEditable(false);
            id1.setBackground(Color.WHITE);
            getListPanel.add(id1);

            JTextField type1 = new JTextField();
            type1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getType());
            type1.setBounds(150,30*(i+1),150,30);
            type1.setEditable(false);
            type1.setBackground(Color.WHITE);
            getListPanel.add(type1);

            JTextField nbPlayer1 = new JTextField();
            nbPlayer1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getPlayers().size());
            nbPlayer1.setBounds(2*150,30*(i+1),150,30);
            nbPlayer1.setEditable(false);
            nbPlayer1.setBackground(Color.WHITE);
            getListPanel.add(nbPlayer1);

            JTextField minBet1 = new JTextField();
            minBet1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getMinBid());
            minBet1.setBounds(3*150,30*(i+1),150,30);
            minBet1.setEditable(false);
            minBet1.setBackground(Color.WHITE);
            getListPanel.add(minBet1);

            JTextField stack1 = new JTextField();
            stack1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getInitStack());
            stack1.setBounds(4*150,30*(i+1),150,30);
            stack1.setEditable(false);
            stack1.setBackground(Color.WHITE);
            getListPanel.add(stack1);

            int index = i;
            JButton joinRoom = new JButton(new AbstractAction() {
                public void actionPerformed(ActionEvent a) {
                    String messageToSend="130 JOIN "+index+1;
                    messageToSend=messageToSend.trim();
                    client.analyseMessageToSend(messageToSend);
                    client.writeToServer(messageToSend);
                    setPanel(roundPanel);
                }
            });
            joinRoom.setText("Rejoindre");
            joinRoom.setBounds(5*150+30,30*(i+1),90,30);
            getListPanel.add(joinRoom);
        }

        JButton create = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                setPanel(createPanel);
            }
        });
        create.setText("Créer une partie");
        create.setBounds(900,50,150,30);
        getListPanel.add(create);

        JButton refresh = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                getList();
            }
        });
        refresh.setText("actualiser");
        refresh.setBounds(900,100,150,30);
        getListPanel.add(refresh);

        setPanel(getListPanel);
    }
}