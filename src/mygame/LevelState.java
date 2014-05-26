/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author jsc
 */
public class LevelState extends AbstractAppState implements ScreenController{
    
    private BulletAppState bas;
    private Node building;
    private Geometry ball;
    private Node rotateNode;
    private RigidBodyControl ballControl;
    private boolean switchObject = false;
    private Node gpointer;
    private int levelNumber;
    private GhostControl goalControl;
    private Quaternion originalBuildingRotation;
    private Vector3f extent;
    private Geometry goal;
    private AssetManager assetManager;
    private Node rootNode;
    private Node guiNode;
    private InputManager inputManager;
    private SimpleApplication app;
    private Camera cam;
    protected BitmapFont guiFont;
    private AppStateManager stateManager;
    private Nifty nifty;
    private Screen screen;
 
    public LevelState(BulletAppState bas, int ln,Node building, Node rn, Nifty nifty){
        this.bas = bas;
        levelNumber = ln;
        this.building = building;
        this.rotateNode = rn;
        this.nifty = nifty;
        screen = nifty.getCurrentScreen();
        this.extent = ((BoundingBox) building.getWorldBound()).getExtent(new Vector3f());
        
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        assetManager = this.app.getAssetManager();
        inputManager = this.app.getInputManager();
        rootNode = this.app.getRootNode();
        cam = this.app.getCamera();
        nifty.setDebugOptionPanelColors(false);
        
        
        guiNode = this.app.getGuiNode();
        this.stateManager = stateManager;
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        Element levelText = screen.findElementByName("levelNumber");
        levelText.getRenderer(TextRenderer.class).setText("Level " + levelNumber);

        
        Sphere sphere = new Sphere(30,30,1f);
        sphere.setTextureMode(Sphere.TextureMode.Projected);
        ball = new Geometry("ball",sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        ball.setMaterial(mat);
        ballControl = new RigidBodyControl(.3f);
        ball.addControl(ballControl);
        rootNode.attachChild(ball);
        bas.getPhysicsSpace().add(ball);
        ballControl.setPhysicsLocation(Main.start[levelNumber-1]);
        ballControl.setLinearDamping(.001f);
        ballControl.setAngularDamping(.001f);
        ballControl.setRestitution(1f);
        ballControl.setGravity(new Vector3f(0,-10f,0));
        
        ChaseCamera camera = new ChaseCamera(cam,ball,inputManager);
        camera.setDefaultDistance(50f);
        setUpKeys();
        
        Sphere sphere2 = new Sphere(30,30,1f);
        sphere2.setTextureMode(Sphere.TextureMode.Projected);
        goal = new Geometry("ball",sphere2);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Magenta);
        goal.setMaterial(mat2);
        goalControl = new GhostControl(new SphereCollisionShape(1f));
        
        rotateNode.attachChild(goal);
        goal.addControl(goalControl);
        bas.getPhysicsSpace().add(goalControl);
        originalBuildingRotation = rotateNode.getLocalRotation();
        System.out.println(originalBuildingRotation);
        goal.setLocalTranslation(Main.goals[levelNumber-1]);
        
        //bas.setDebugEnabled(true);
        
        gpointer = (Node)assetManager.loadModel("Models/arrow.j3o");
        gpointer.setLocalScale(gpointer.getLocalScale().multLocal(5,-5,5));
        System.out.println(cam.getLocation() + " " + cam.getFrustumLeft());
        Element panel = screen.findElementByName("panel_top");
        Element g = screen.findElementByName("gravity");
        gpointer.setLocalTranslation(new Vector3f
                (g.getX()+g.getWidth()/2f,app.getContext().getSettings().getHeight()-g.getHeight(),0));
        //gpointer.setLocalTranslation(20,10,20);
        /*Quaternion setup = new Quaternion();
        setup.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
        gpointer.rotate(setup);*/
        guiNode.attachChild(gpointer);
        
    }
    
    
    @Override
    public void update(float tpf) {
        float angle = cam.getUp().angleBetween(Vector3f.UNIT_Y);
        Quaternion rotatePointer = new Quaternion();
        rotatePointer.fromAngleAxis(FastMath.TWO_PI-angle,Vector3f.UNIT_X);
        System.out.println("Angle from y: " + angle*FastMath.RAD_TO_DEG + " " + rotatePointer);
        gpointer.setLocalRotation(rotatePointer);
        
        if(goalControl.getOverlappingObjects().contains(ballControl)){
            levelNumber++;
            LevelState ls = new LevelState(bas,levelNumber,building,rotateNode,nifty);
            stateManager.detach(this);
            stateManager.attach(ls);
        }
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();

        guiNode.detachChild(gpointer);
        rotateNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        Quaternion rotateZ90 = new Quaternion();
        rotateNode.setLocalRotation(rotateZ90);
        building.setLocalTranslation(0, -extent.getY(), 0);
        rotateNode.detachChild(goal);
        rootNode.detachChild(ball);
        bas.getPhysicsSpace().remove(goalControl);
        bas.getPhysicsSpace().remove(ballControl);
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);

    }
    
    
    
    private void setUpKeys() {
        
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Switch", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(analogListener,"Left");
        inputManager.addListener(analogListener,"Right");
        inputManager.addListener(actionListener,"Switch");
        inputManager.addListener(actionListener,"Reset");
        inputManager.addListener(analogListener,"Down");
        inputManager.addListener(analogListener,"Up");

    }
    
    private ActionListener actionListener = new ActionListener(){

        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Switch") && !isPressed){
                System.out.println(switchObject);
                switchObject = !switchObject;
                if(switchObject){
                    ballControl.setEnabled(false);
                    ballControl.setGravity(Vector3f.ZERO);
                    
                } else {
                    ballControl.setEnabled(true);
                    ballControl.setGravity(new Vector3f(0,-10,0));
                }
            } else if(name.equals("Reset") && !isPressed){
                reset();
            }
        }
        
        
    };
    
    private AnalogListener analogListener = new AnalogListener() {

        public void onAnalog(String name, float value, float tpf) {
            
            if(!switchObject){
                moveBall(name,value,tpf);
            } else {
                moveBuilding(name,value,tpf);
            }
            
        }
        
        private void moveBall(String name, float value, float tpf){
            if(name.equals("Left")){
                Vector3f dir = cam.getLeft();
                dir.setY(0);
                dir.normalizeLocal();
                ballControl.applyForce(dir.multLocal(10), new Vector3f(0,0,0));
            } else if(name.equals("Right")){
                Vector3f dir = cam.getLeft();
                dir.setY(0);
                dir.normalizeLocal();
                ballControl.applyForce(dir.multLocal(-10), new Vector3f(0,0,0));
            } else if(name.equals("Up")){
                Vector3f dir = cam.getLeft().cross(cam.getUp());
                dir.setY(0);
                dir.normalizeLocal();
                
                ballControl.applyForce(dir.multLocal(10), new Vector3f(0,0,0));
            } else if(name.equals("Down")){
                Vector3f dir = cam.getLeft().cross(cam.getUp());
                dir.setY(0);
                dir.normalizeLocal();
                ballControl.applyForce(dir.multLocal(-10), new Vector3f(0,0,0));
            } 
        }
        
         private void moveBuilding(String name, float value, float tpf){
            if(name.equals("Up")){
                Vector3f dir = cam.getLeft();
                Quaternion up = new Quaternion();
                dir.setY(0);
                dir.normalizeLocal().multLocal(tpf);
                rotateNode.rotate(dir.getX(),dir.getY(),dir.getZ());
            } else if(name.equals("Down")){
                Vector3f dir = cam.getLeft();
                dir.setY(0);
                dir.normalizeLocal().multLocal(-tpf);
                rotateNode.rotate(dir.getX(),dir.getY(),dir.getZ());            
            } else if(name.equals("Left")){
                Vector3f dir = cam.getUp();
                dir.setY(0);
                dir.normalizeLocal().multLocal(tpf);
                rotateNode.rotate(dir.getX(),dir.getY(),dir.getZ());
            } else if(name.equals("Right")){
                Vector3f dir = cam.getUp();
                dir.setY(0);
                dir.normalizeLocal().multLocal(-tpf);
                rotateNode.rotate(dir.getX(),dir.getY(),dir.getZ());
            }
         }

    };

    public int getLevelNumber(){
        return levelNumber;
    }
    
    public void reset(){
        LevelState ls = new LevelState(bas,levelNumber,building,rotateNode,nifty);
        //nifty.fromXml("Interface/LevelScreen.xml", "start"); 
        stateManager.detach(this);
        stateManager.attach(ls);
    }
    
    public void bind(Nifty nifty, Screen screen) {
        System.out.println("BIND CALLED");
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }
}
