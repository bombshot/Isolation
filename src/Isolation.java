
import java.awt.Color;
import java.awt.Dialog;
import java.util.concurrent.Semaphore;
import javax.swing.JOptionPane;

/**
 *
 * @author vpsingh
 */
public class Isolation{
    
    static int dx[] = {2, 1, -1, -2, -2, -1, 1, 2};
    static int dy[] = {1, 2, 2, 1, -1, -2, -2, -1};
    static int human = 1;
    static int robot = 2;
    class Position {
        int x, y;
        Position() {
            x = -1;
            y = -1;
        }
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    class MoveScore {
        Position p;
        int score;
        MoveScore() {
            p = new Position();
            score = 0;
        }
    }
    
    public Position findPosition(int pl, int mat[][]) {
        
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(mat[i][j] == pl) {
                    return new Position(i, j);
                }
            }
        }
        return new Position();
    }
    
    public int findscore(int mat[][], Position p) {
        int res = 0;
        int x, y;
        for(int i = 0; i < 8; i++) {
            x = p.x + dx[i];
            y = p.y + dy[i];
            if(x < 0 || x > 7 || y < 0 || y > 7) continue;
            if(mat[x][y] == 0) {
                res++;
            }
        }
        return res;
    }
    
    public int minimax(int mat[][], boolean ismax, int depth, int depthlimit) {
        
        Position hpos = findPosition(human, mat);
        Position rpos = findPosition(robot, mat);
        
        int rscore = findscore(mat, rpos);
        int hscore = findscore(mat, hpos);
        if(hscore == 0) return 100;
        if(rscore == 0) return -100;
        
        if(depth >= depthlimit && ismax) {
            return rscore - hscore;
        }
        else if(depth >= depthlimit) {
            return hscore - rscore;
        }
        
        if(ismax) {
            
            int best = -1000000007;
            
            int nx, ny;
            boolean pos = false;
            for(int i = 0; i < 8; i++) {
                nx = rpos.x + dx[i];
                ny = rpos.y + dy[i];
                if(nx < 0 || nx > 7 || ny < 0 || ny > 7) continue;
                if(mat[nx][ny] == 0) {
                    pos = true;
                    mat[nx][ny] = robot;
                    mat[rpos.x][rpos.y] = -1;
                    int res = minimax(mat, false, depth + 1, depthlimit);
                    if(res > best) {
                        best = res;
                    }
                    
                    mat[nx][ny] = 0;
                    mat[rpos.x][rpos.y] = robot;
                }
            }
            
            return best;
        }
        else {
            
            int best = 1000000007;
            
            int nx, ny;
            boolean pos = false;
            for(int i = 0; i < 8; i++) {
                nx = hpos.x + dx[i];
                ny = hpos.y + dy[i];
                if(nx < 0 || nx > 7 || ny < 0 || ny > 7) continue;
                if(mat[nx][ny] == 0) {
                    pos = true;
                    mat[nx][ny] = human;
                    mat[hpos.x][hpos.y] = -1;
                    int res = minimax(mat, true, depth + 1, depthlimit);
                    if(res < best) {
                        best = res;
                    }
                    
                    mat[nx][ny] = 0;
                    mat[hpos.x][hpos.y] = human;
                }
            }
            return best;
        }  
    }
    
    
    public MoveScore findBestMove(int[][] mat, int d) {
        
        MoveScore optimal = new MoveScore();
        Position hpos = findPosition(human, mat);
        Position rpos = findPosition(robot, mat);
        
        int nx, ny, best = -1000000007;
        
        for(int i = 0; i < 8; i++) {
            nx = rpos.x + dx[i];
            ny = rpos.y + dy[i];
            if(nx < 0 || nx > 7 || ny < 0 || ny > 7) continue;
            
            if(mat[nx][ny] == 0) {
                
                mat[nx][ny] = robot;
                mat[rpos.x][rpos.y] = -1;
                int val = minimax(mat, false, 0, d);


                if(val > best) {
                    best = val;
                    optimal.p = new Position(nx, ny);
                    optimal.score = val;
                }
                
                mat[nx][ny] = 0;
                mat[rpos.x][rpos.y] = robot;
            }
            
        }
        
        return optimal;
    }
    
    public Position iterativeDeepening(int mat[][]) {
        Position res = new Position();
        int best = -100000000;
        MoveScore obj;
        for(int d = 1; d <= 7; d += 2) {
            obj = findBestMove(mat, d);
            if(obj.score > best) {
                best = obj.score;
                res = obj.p;
            }
        }
        return res;
    }
    
    
    public void solve(GridWindow grid) throws Throwable{
        
        int mat[][] = new int[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                mat[i][j] = grid.buttons[i][j].status;
                //System.out.print(mat[i][j]);
            }
            //System.out.println();
        }
        
        Position nextpos = iterativeDeepening(mat);
        if(nextpos.x == -1) {
              //System.out.println("YOU WON");
              grid.gameover = true;
              grid.frame.setEnabled(false);
              return;
        }
        Position rpos = findPosition(robot, mat);
        grid.buttons[rpos.x][rpos.y].status = -1;
        grid.buttons[rpos.x][rpos.y].button.setBackground(Color.LIGHT_GRAY);
        grid.buttons[rpos.x][rpos.y].button.setIcon(null);
        
        grid.buttons[nextpos.x][nextpos.y].status = robot;
        grid.buttons[nextpos.x][nextpos.y].button.setEnabled(false);
        grid.buttons[nextpos.x][nextpos.y].button.setIcon(grid.bkn);
        //System.out.println(nextpos.x + " " + nextpos.y);
        
    }
    
    public static void main(String args[]) throws Throwable {
        
        GridWindow grid = new GridWindow();
        Isolation isola = new Isolation();
        
        boolean humanwins = true;
        while(grid.gameover == false) {
            
            if(grid.humanchance) {
                grid.s.acquire();
            }
            else {
                for(int i = 0; i < 8; i++) {
                    for(int j = 0; j < 8; j++) {
                        if(grid.buttons[i][j].status == human) {
                            grid.plx = i;
                            grid.ply = j;
                        }
                    }
                }
                isola.solve(grid);
                boolean flag = false;
                for(int i = 0; i < 8; i++) {
                    int nx = grid.plx + dx[i];
                    int ny = grid.ply + dy[i];
                    if(nx < 0 || nx > 7 || ny < 0 || ny > 7) continue;
                    if(grid.buttons[nx][ny].status == 0) {
                        flag = true;
                        grid.buttons[nx][ny].status = 3;
                        grid.buttons[nx][ny].button.setBackground(Color.CYAN);
                        grid.buttons[nx][ny].button.setEnabled(true);
                    }
                }
                
                if(!flag) {
                    //System.out.println("You Lose");
                    grid.frame.setEnabled(false);
                    grid.gameover = true;
                    humanwins = false;
                }
                grid.humanchance = true;
            }
        }
        if(humanwins) {
            JOptionPane.showMessageDialog(null,"You Won");
        }
        else {
            JOptionPane.showMessageDialog(null, "You Lose");
        }
        System.exit(0);
    }
  
}
