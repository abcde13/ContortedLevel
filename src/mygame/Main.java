package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import de.lessvoid.nifty.Nifty;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private Node building;
    private Vector3f extent;
    private Node rotateNode;
    private BulletAppState bas;
    public static Vector3f[] start = {new Vector3f(-5,50,-5),new Vector3f(-5,50,-5),new Vector3f(-5,50,-5)};
    public static Vector3f[] goals = new Vector3f[3]; 
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
      
        setDisplayFps(false);
        setDisplayStatView(false);
        bas = new BulletAppState();
        stateManager.attach(bas);
        flyCam.setEnabled(false);
        initScene();
        
        goals[0] = new Vector3f(0f, -extent.getY()-2f, 0f);
        goals[1] = new Vector3f(10f,10f,10f);
        goals[2] = new Vector3f(20,2,0);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
            assetManager, inputManager, audioRenderer, guiViewPort);
        /** Create a new NiftyGUI object */
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        
        LevelState levels = new LevelState(bas,1,building,rotateNode,nifty);
        stateManager.attach(levels);
        
        
        nifty.registerScreenController(levels);
        nifty.fromXml("Interface/LevelScreen.xml", "start"); 
        
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void initScene(){
        
        building = (Node)assetManager.loadModel("Models/Tower.j3o");
        extent = ((BoundingBox) building.getWorldBound()).getExtent(new Vector3f());
        
        CompoundCollisionShape sceneShape = new CompoundCollisionShape();
        BoxCollisionShape floor = new BoxCollisionShape(new Vector3f(extent.getX(),.5f,extent.getZ()));
        BoxCollisionShape onestory = new BoxCollisionShape(
                new Vector3f(extent.getX()/4f+2.5f,extent.getY()/5f-1.5f,extent.getZ()/4f+2f));
        sceneShape.addChildShape(onestory, new Vector3f(-extent.getX()/2+3.2f,extent.getY()/5+3f,extent.getZ()/2-3));
        sceneShape.addChildShape(floor, Vector3f.ZERO);
        BoxCollisionShape twostory = new BoxCollisionShape(
                new Vector3f(extent.getX()/4f+2.5f,extent.getY()/2f-1.5f,extent.getZ()/4f+1.5f));
        sceneShape.addChildShape(twostory, new Vector3f(extent.getX()/2-3.2f,extent.getY()/2-1f,extent.getZ()/2-3));
        BoxCollisionShape threestory = new BoxCollisionShape(
                new Vector3f(extent.getX()/4f+2.5f,extent.getY()/1.5f,extent.getZ()/4f+1.5f));
        sceneShape.addChildShape(threestory, new Vector3f(extent.getX()/2-3.5f,extent.getY()/1.5f,-extent.getZ()/2+3f));
        BoxCollisionShape fourstory = new BoxCollisionShape(
                new Vector3f(extent.getX()/4f+2.5f,extent.getY()/1.1f,extent.getZ()/4f+2f));
        sceneShape.addChildShape(fourstory, new Vector3f(-extent.getX()/2+3.5f,extent.getY()/1.1f-.5f,-extent.getZ()/2+3f));
        BoxCollisionShape topCube = new BoxCollisionShape(
                new Vector3f(extent.getX()/9f,extent.getY()/8.5f,extent.getZ()/9f));
        sceneShape.addChildShape(topCube, new Vector3f(-extent.getX()/2+3.5f,2*extent.getY()*.95f,-extent.getZ()/2+3.5f));
        BoxCollisionShape tree = new BoxCollisionShape(
                new Vector3f(extent.getX()/24f,extent.getY()/14f,extent.getZ()/24f));
        int i = 0;
        float j = extent.getZ()-3f;
        while(i < 6){
            if(i == 0 || i == 5){
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+3f,1,j));
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+11f,1,j));
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+17f,1,j)); 
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+25f,1,j));
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+31f,1,j)); 
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+39f,1,j)); 
            } else if (i == 1 || i==2){
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+3f,1,j));
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+11f,2*extent.getY()/5f+2f,j));
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+17f,2*extent.getY()/5f+2f,j)); 
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+25f,2*extent.getY()/2f-1f,j));
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+31f,2*extent.getY()/2f-1f,j)); 
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+39f,1,j));
            } else if (i == 3 || i==4){
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+3f,1,j));
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+25f,2*extent.getY()/1.5f+1f,j));
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+31f,2*extent.getY()/1.5f+1f,j)); 
                sceneShape.addChildShape(tree,new Vector3f(-extent.getX()+39f,1,j));
            }
            if(i%2 == 0){
                j -= 8;
            } else {
                j -= 6;
            }
            i++;
        }
        
        //Level 1 overhangings
        BoxCollisionShape overhangingx = new BoxCollisionShape(
                new Vector3f(extent.getX()/3.5f,.2f,extent.getZ()/20f));
        sceneShape.addChildShape(overhangingx,new Vector3f(-extent.getX()/2+3.2f,extent.getY()/5f+5f,extent.getZ()/2f+4.5f));
        BoxCollisionShape overhangingxrail = new BoxCollisionShape(
                new Vector3f(extent.getX()/3.5f,1f,.2f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(-extent.getX()/2+3.2f,extent.getY()/5f+6f,extent.getZ()/2f+5.5f));
        sceneShape.addChildShape(overhangingx,new Vector3f(-extent.getX()/2+3.2f,extent.getY()/5f+5f,-extent.getZ()/2-5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(-extent.getX()/2+3.2f,extent.getY()/5f+6f,-extent.getZ()/2-6f));        
        sceneShape.addChildShape(overhangingx,new Vector3f(extent.getX()/4f+2.5f,extent.getY()/5f+5f,extent.getZ()/2f+4.5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(extent.getX()/4f+2.5f,extent.getY()/5f+6f,extent.getZ()/2f+5.5f));
        sceneShape.addChildShape(overhangingx,new Vector3f(extent.getX()/4f+2.5f,extent.getY()/5f+5f,-extent.getZ()/2-5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(extent.getX()/4f+2.5f,extent.getY()/5f+6f,-extent.getZ()/2-6f));
        
        
        
        //Level 2 overhangings
        sceneShape.addChildShape(overhangingx,new Vector3f(extent.getX()/4f+2.5f,2*extent.getY()/2-2.5f,extent.getZ()/2f+4.5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(extent.getX()/4f+2.5f,2*extent.getY()/2-1.5f,extent.getZ()/2f+5.5f));
        sceneShape.addChildShape(overhangingx,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/2-2.5f,-extent.getZ()/2-5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/2-1.5f,-extent.getZ()/2-6f));
        sceneShape.addChildShape(overhangingx,new Vector3f(extent.getX()/4f+2.5f,2*extent.getY()/2-2.5f,-extent.getZ()/2-5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(extent.getX()/4f+2.5f,2*extent.getY()/2-1.5f,-extent.getZ()/2-6f));
        
        
        
        //Level 3 overhanings
        sceneShape.addChildShape(overhangingx,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/1.5f,-extent.getZ()/2+11f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/1.5f+1f,-extent.getZ()/2+12f));   
        sceneShape.addChildShape(overhangingx,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/1.5f,-extent.getZ()/2-5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/1.5f+1f,-extent.getZ()/2-6f));
        sceneShape.addChildShape(overhangingx,new Vector3f(extent.getX()/2-3.5f,2*extent.getY()/1.5f,-extent.getZ()/2+11f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(extent.getX()/2-3.5f,2*extent.getY()/1.5f+1f,-extent.getZ()/2+12f));
        sceneShape.addChildShape(overhangingx,new Vector3f(extent.getX()/2-3.5f,2*extent.getY()/1.5f,-extent.getZ()/2-5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(extent.getX()/2-3.5f,2*extent.getY()/1.5f+1f,-extent.getZ()/2-6f));

        //Level 4 overhangings 
        sceneShape.addChildShape(overhangingx,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/1.1f-.5f,-extent.getZ()/2+11f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/1.1f+.5f,-extent.getZ()/2+12f));
        sceneShape.addChildShape(overhangingx,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/1.1f-.5f,-extent.getZ()/2-5f));
        sceneShape.addChildShape(overhangingxrail,new Vector3f(-extent.getX()/2+3.2f,2*extent.getY()/1.1f+.5f,-extent.getZ()/2-6f));
        

        
        
        RigidBodyControl scenePhysics = new RigidBodyControl(sceneShape,1f);
        scenePhysics.setKinematic(true);
        building.addControl(scenePhysics);
        rotateNode = new Node("rotation node for building");
        rotateNode.setLocalTranslation(0, extent.getY(), 0);
        rotateNode.attachChild(building);
        building.setLocalTranslation(0, -extent.getY(), 0);
        rootNode.attachChild(rotateNode);
       
        bas.getPhysicsSpace().add(scenePhysics);
        
        
        
        
    }
    
    
    
    private void attachCoordinateAxes(Vector3f pos){
        Arrow arrow = new Arrow(Vector3f.UNIT_X);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Red).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Y);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Green).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Z);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Blue).setLocalTranslation(pos);
      }

      private Geometry putShape(Mesh shape, ColorRGBA color){
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        rootNode.attachChild(g);
        return g;
      }
    
    
}
