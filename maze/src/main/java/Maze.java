
import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class Maze extends JFrame {

    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;

    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }

    public void checkWayOut() {
        new Thread(() -> {
            boolean result = false;
            switch (this.algorithm) {
                case Definitions.ALGORITHM_DFS:
                    result = this.solveDFS();
                    break;
                case Definitions.ALGORITHM_BFS:
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
    }


    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isEndOfMaze(MyPoint point){
        return point.getX() == this.columns-1 && point.getY() == this.rows-1;
    }
    private boolean isEmptyPoint(MyPoint point){
        return this.values[point.getX()][point.getY()] == Definitions.EMPTY;
    }
    private boolean isPointInside(MyPoint point){
        return (point.getX() >= 0 && point.getY() >= 0) && (point.getX() < this.columns && point.getY() < this.rows);
    }
    private boolean isPointVisited(MyPoint point){
        return this.visited[point.getX()][point.getY()];
    }

    private ArrayList<MyPoint> getNeighborPoints(MyPoint currentPoint){
        ArrayList<MyPoint> availablePoints = new ArrayList<>();
        ArrayList<MyPoint> list = new ArrayList<>();

        int x = currentPoint.getX();
        int y = currentPoint.getY();
        availablePoints.add(new MyPoint(x,y-1));
        availablePoints.add(new MyPoint(x-1,y));
        availablePoints.add(new MyPoint(x,y+1));
        availablePoints.add(new MyPoint(x+1,y));

        for (MyPoint point: availablePoints){
            if (this.isPointInside(point) && this.isEmptyPoint(point) && !this.isPointVisited(point)) {
                list.add(new MyPoint(point.getX(), point.getY()));
            }
        }

        return list;
    }

    private boolean solveDFS(){
        boolean result = false;

        Stack<MyPoint> pointStack = new Stack<>();
        pointStack.add(new MyPoint(0,0));

        while (!pointStack.isEmpty()){
            MyPoint currentPoint = pointStack.pop();
            if (!this.isPointVisited(currentPoint)){
                setSquareAsVisited(currentPoint.getX(), currentPoint.getY(), true);
                if (this.isEndOfMaze(currentPoint)){
                    result = true;
                    break;
                }

                ArrayList<MyPoint> neighborPoints = this.getNeighborPoints(currentPoint);
                for (MyPoint point: neighborPoints){
                    pointStack.add(point);
                }
            }
        }

        return result;
    }
}
