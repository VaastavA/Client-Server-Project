public class TicTacToeGame extends Thread{

    private static final char PLAYERX = 'X';     // Helper constant for X player
    private static final char PLAYERO = 'O';     // Helper constant for O player
    private static final char SPACE = ' ';       // Helper constant for spaces
    private String player1;
    private String player2;
    private char[][] gameBoard;
    private int turnCounter;
    public Object lock = new Object();

    /*
    Sample TicTacToe Board
      0 | 1 | 2
     -----------
      3 | 4 | 5
     -----------
      6 | 7 | 8
     */

    // TODO 4: Implement necessary methods to manage the games of Tic Tac Toe
    public TicTacToeGame(String player1,String player2){
        this.player1=player1;
        this.player2=player2;
        this.gameBoard=new char[3][3];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                gameBoard[i][j]=SPACE;
            }
        }
        this.turnCounter=0;
    }

    public int takeTurn(int index) {
        synchronized (lock) {
            if (turnCounter % 2 == 0) {
                switch (index) {
                    case 0:
                        gameBoard[0][0] = PLAYERX;
                        break;
                    case 1:
                        gameBoard[0][1] = PLAYERX;
                        break;
                    case 2:
                        gameBoard[0][2] = PLAYERX;
                        break;
                    case 3:
                        gameBoard[1][0] = PLAYERX;
                        break;
                    case 4:
                        gameBoard[1][1] = PLAYERX;
                        break;
                    case 5:
                        gameBoard[1][2] = PLAYERX;
                        break;
                    case 6:
                        gameBoard[2][0] = PLAYERX;
                        break;
                    case 7:
                        gameBoard[2][1] = PLAYERX;
                        break;
                    case 8:
                        gameBoard[2][2] = PLAYERX;
                        break;
                }

            } else if (turnCounter % 2 == 1) {
                switch (index) {
                    case 0:
                        gameBoard[0][0] = PLAYERO;
                        break;
                    case 1:
                        gameBoard[0][1] = PLAYERO;
                        break;
                    case 2:
                        gameBoard[0][2] = PLAYERO;
                        break;
                    case 3:
                        gameBoard[1][0] = PLAYERO;
                        break;
                    case 4:
                        gameBoard[1][1] = PLAYERO;
                        break;
                    case 5:
                        gameBoard[1][2] = PLAYERO;
                        break;
                    case 6:
                        gameBoard[2][0] = PLAYERO;
                        break;
                    case 7:
                        gameBoard[2][1] = PLAYERO;
                        break;
                    case 8:
                        gameBoard[2][2] = PLAYERO;
                        break;
                }
            }
            turnCounter++;
            return turnCounter;

        }
    }

    public char getWinner(){
        char winner=SPACE;
        if(gameBoard[0][0]==gameBoard[0][1]&&gameBoard[0][1]==gameBoard[0][2]&&gameBoard[0][0]!=SPACE){
            winner=gameBoard[0][0];
        }
        else if(gameBoard[1][0]==gameBoard[1][1]&&gameBoard[1][1]==gameBoard[1][2]&&gameBoard[1][0]!=SPACE){
            winner=gameBoard[1][0];
        }
        else if(gameBoard[2][0]==gameBoard[2][1]&&gameBoard[2][1]==gameBoard[2][2]&&gameBoard[2][0]!=SPACE){
            winner=gameBoard[2][0];
        }
        else if(gameBoard[0][0]==gameBoard[1][0]&&gameBoard[1][0]==gameBoard[2][0]&&gameBoard[0][0]!=SPACE){
            winner=gameBoard[0][0];
        }
        else if(gameBoard[0][1]==gameBoard[1][1]&&gameBoard[1][1]==gameBoard[2][1]&&gameBoard[0][1]!=SPACE){
            winner=gameBoard[0][1];
        }
        else if(gameBoard[0][2]==gameBoard[1][2]&&gameBoard[1][2]==gameBoard[2][2]&&gameBoard[0][2]!=SPACE){
            winner=gameBoard[0][2];
        }
        else if(gameBoard[0][0]==gameBoard[1][1]&&gameBoard[1][1]==gameBoard[2][2]&&gameBoard[0][0]!=SPACE){
            winner=gameBoard[0][0];
        }
        else if(gameBoard[2][0]==gameBoard[1][1]&&gameBoard[1][1]==gameBoard[0][2]&&gameBoard[2][0]!=SPACE){
            winner=gameBoard[0][0];
        }
        return winner;

    }

    public int isTied(){
        if(turnCounter==9&&getWinner()==SPACE){
            return 1;
        }
        else{
            return 0;
        }

    }

    public boolean isValidMove(int index){
        char a=' ';
        switch (index){
            case 0:a=gameBoard[0][0];
            case 1:a=gameBoard[0][1];
            case 2:a=gameBoard[0][2];
            case 3:a=gameBoard[1][0];
            case 4:a=gameBoard[1][1];
            case 5:a=gameBoard[1][2];
            case 6:a=gameBoard[2][0];
            case 7:a=gameBoard[2][1];
            case 8:a=gameBoard[2][2];

        }
        if(a==SPACE)
        {
            return true;
        }
        else
        {
            return false;
        }

    }
    public boolean playerOneMove()
    {
        if(getTurnCounter()%2==0) {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String toString() {
        synchronized (lock){
        return "\n"+gameBoard[0][0] + "|" + gameBoard[0][1] + "|" + gameBoard[0][2] + "\n" + "------" + "\n" + gameBoard[1][0] + '|' + gameBoard[1][1] + '|' + gameBoard[1][2] + '\n' + "------" + "\n" + gameBoard[2][0] + "|" + gameBoard[2][1] + "|" + gameBoard[2][2];
    }
    }

    public boolean isGameOver()
    {
        if(isTied()==1 || getWinner()==PLAYERO || getWinner()==PLAYERX)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
