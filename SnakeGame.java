import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import java.util.LinkedList;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
public class SnakeGame extends Application{
	private LinkedList<SnakePart> snakeParts=new LinkedList<>();
	private Direction direction=Direction.RIGHT;
	private SnakePart food;
	private long previousTimeFood;
	private int counterOfApple=3;
	private int x;
	private int y;
	private long speed=(long)1e8;
	private Canvas canvas;
	private GraphicsContext context;
	private Image snakePartPhoto=new Image("SnakePart.png");
	private Image applePhoto=new Image("apple_png.png");
	private Image headImege=new Image("snake_RIGHT.png"); 
	private Image background=new Image("SnakeBackGround.png");
	private boolean gameOver=false;
	private boolean textInTerminal=false;
	public void initSnake(){
		for(int i=2;i>=0;i--){
			snakeParts.add(new SnakePart(i,0));
		}												
	}
	public void start(Stage s){
		canvas=new Canvas(600,600);
		context=canvas.getGraphicsContext2D();
		Pane pane=new Pane(canvas);
		initSnake();
		newFood();
		Scene scene=new Scene(pane,600,600);
		AnimationTimer time=new AnimationTimer(){
			long previousTime=0;
			public void handle(long now){
				if(now-previousTime>=speed){
					moveSnake();
					displayScreen(context);
					previousTime=now;
				}
			}
		};
		time.start();
		scene.setOnKeyPressed(i->{
			KeyCode userInput=i.getCode();
			switch(userInput){
				case W:if(direction!=Direction.DOWN) direction=Direction.UP;break;
				case S:if(direction!=Direction.UP) direction=Direction.DOWN;break;
				case A: if(direction!=Direction.RIGHT) direction=Direction.LEFT;break;
				case D: if(direction!=Direction.LEFT) direction=Direction.RIGHT;break;
			}
		});
		Image iconPhoto =new Image("snake_icon.png");
		s.getIcons().add(iconPhoto);
		s.setScene(scene);	
		s.setTitle("Aman's project");
		s.show();
	}public void moveSnake(){
		SnakePart head=snakeParts.getFirst();
		SnakePart headOne;
		if(direction==Direction.UP){
			headOne=new SnakePart(head.getCordinationOfX(),(head.getCordinationOfY()-1+20)%20);
			headImege=new Image("snake_UP.png");
		}else if(direction==Direction.DOWN){
			headOne=new SnakePart(head.getCordinationOfX(),(head.getCordinationOfY()+1)%20);
			headImege=new Image("snake_DOWN.png");
		}else if(direction==Direction.LEFT){
			headOne=new SnakePart((head.getCordinationOfX()-1+20)%20,head.getCordinationOfY());
			headImege=new Image("snake_LEFT.png");
		}else if (direction==Direction.RIGHT){
			headOne=new SnakePart((head.getCordinationOfX()+1)%20,head.getCordinationOfY());
			headImege=new Image("snake_RIGHT.png");
		}else{
			headOne=head;
		}
		if(headOne.getCordinationOfX()==food.getCordinationOfX()&& headOne.getCordinationOfY()==food.getCordinationOfY()){
			snakeParts.addFirst(headOne);
			newFood();	
			counterOfApple++;
			System.out.println(counterOfApple);
			speed-=2e6;
		}else{
			snakeParts.addFirst(headOne);
			snakeParts.removeLast(); 
		}
		intersectsSnakeToItsPart();
		long myTimer=System.nanoTime();
		if(myTimer-previousTimeFood>=7e9){
			newFood();
		}
	}
	public void intersectsSnakeToItsPart(){
		SnakePart head=snakeParts.getFirst();
		for(int i=1;i<snakeParts.size();i++){
			SnakePart body=snakeParts.get(i); 
			if(head.getCordinationOfX()==body.getCordinationOfX()&& head.getCordinationOfY()==body.getCordinationOfY()){
				gameOver();
			}
		}	
	}
	public void newFood(){
		for(int i=0;;i++){
			x=(int)(Math.random()*20);
			y=(int)(Math.random()*20);
			if(!isFoodInSnakePart(x,y)){
				break;
			}
		}
		food=new SnakePart(x,y);
		previousTimeFood=System.nanoTime();
	}
	public boolean isFoodInSnakePart(int x,int y){
		for(SnakePart eachPart:snakeParts){
			if(eachPart.getCordinationOfY()==y && eachPart.getCordinationOfX()==x){
				return true;
			}
		}
		return false;
    }
    public void gameOver(){
    	if(!textInTerminal){
			System.out.println("Inforamtions about snake:");
			System.out.println("Snake lenght: "+ counterOfApple);
			System.out.println("Eaten apple: "+(counterOfApple-3));
			System.out.println("Speed(move per time) : "+(speed/1e8)+" milliseconds");
			System.out.println("Game over");
			System.out.println("Aman SUPER your project worth 10 points and bonus point overall 11 points!!!.\nYou did it your best!) ");
		}
		gameOver=true;
		textInTerminal=true;
    }public void  displayScreen(GraphicsContext context){
		if(gameOver){
			context.setFill(Color.RED);
			context.setFont(new Font("",50));
			context.fillText("Game over",180,300);
			return;
		}
		context.clearRect(0,0,600,600);
		context.drawImage(background,0,0,600,600);
		SnakePart head=snakeParts.getFirst();
		context.drawImage(headImege,head.getCordinationOfX()*30,head.getCordinationOfY()*30,30,30);
		for(int i=1;i<snakeParts.size();i++){
			SnakePart body=snakeParts.get(i);
			context.drawImage(snakePartPhoto,body.getCordinationOfX()*30,body.getCordinationOfY()*30,28,28);
		}
		context.drawImage(applePhoto,food.getCordinationOfX()*30,food.getCordinationOfY()*30,30,30);
		context.setFill(Color.WHITE);
		context.fillText("Inforamtions about snake:",30,10);
		context.fillText(("Current lenght:"+ counterOfApple),30,30);
		context.fillText(("Eaten apple: "+(counterOfApple-3)),30,50);
		context.fillText("Speed(move per time): "+(speed/1e8)+" milliseconds",30,70);
	}
	public static void main(String[] args){
		launch(args);
	}
	enum Direction{
		UP,LEFT,RIGHT,DOWN,
	}
}
