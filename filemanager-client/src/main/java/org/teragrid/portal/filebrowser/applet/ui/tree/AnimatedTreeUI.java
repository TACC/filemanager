package org.teragrid.portal.filebrowser.applet.ui.tree;

/**

 * Copyright Neil Cochrane 2006

 */

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

/**

 * Animates the expansion and collapse of tree nodes.

 * 

 * Painting is hijacked after the user expands/collapses a node. Images are

 * copied from the expanded tree and used to composite a new image of the 

 * lower part slowly sliding out from beneath the top part.

 */

public class AnimatedTreeUI extends BasicTreeUI

{

    private BufferedImage _topImage;

    private BufferedImage _bottomImage;

    private BufferedImage _compositeImage;

    

    private int _offsetY; // amount to offset bottom image position during animation

    private int _subTreeHeight; // height of newly exposed subtree

    

    private Timer _timer;

    private ActionListener _timerListener;

    

    private enum AnimationState {EXPANDING, COLLAPSING, NOT_ANIMATING};

    private AnimationState _animationState;

    

    private float _animationComplete = 0f; // animation progresses 1f -> 0f

    public static float ANIMATION_SPEED = 0.66f; // nearer 1f = faster, 0f = slower

        

    /** Creates a new instance of AnimatedTree */

    public AnimatedTreeUI() {

      super();

      _animationState = AnimatedTreeUI.AnimationState.NOT_ANIMATING;

      _timerListener = new TimerListener();

      _timer = new Timer(1000/90, _timerListener);

    }

    

    /**

     * All expand and collapsing done by the UI comes through here.

     * Use it to trigger the start of animation

     * @param path

     */

    protected void toggleExpandState(TreePath path) {

      if (_animationState != AnimatedTreeUI.AnimationState.NOT_ANIMATING)

        return;



      _animationComplete = 1f;

      boolean state = !tree.isExpanded(path);

              

      if (state){

        super.toggleExpandState(path);

        _subTreeHeight = _getSubTreeHeight(path);

        _createImages(path);

        _animationState = AnimatedTreeUI.AnimationState.EXPANDING;

      }

      else{

        _subTreeHeight = _getSubTreeHeight(path);

        _createImages(path);

        super.toggleExpandState(path);

        _animationState = AnimatedTreeUI.AnimationState.COLLAPSING;

      }

      

      _updateCompositeImage();

      _timer.restart();

    }

    

    /**

     * Grab two images from the tree. The bit below the expanded node

     * (_bottomImage), and the rest above it (_topImage)

     */

    private void _createImages(TreePath path){

      int h = tree.getPreferredSize().height;

      int w = tree.getPreferredSize().width;

      BufferedImage baseImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

      Graphics g = baseImage.getGraphics();

      tree.paint(g);

      

      // find the next row, this is where we take the overlay image from

      int row = tree.getRowForPath(path)+1;

      _offsetY = tree.getRowBounds(row).y;

      

      _topImage = new BufferedImage(tree.getWidth(),

        _offsetY, BufferedImage.TYPE_INT_RGB);

      Graphics topG = _topImage.getGraphics();

      topG.drawImage(baseImage,

          0, 0, w, _offsetY,  // dest

          0, 0, w, _offsetY,  // src

          null);  

     

      _bottomImage = new BufferedImage(w,

        baseImage.getHeight() - _offsetY, BufferedImage.TYPE_INT_RGB);

      Graphics bottomG = _bottomImage.getGraphics();

      bottomG.drawImage(baseImage,

          0, 0, w, baseImage.getHeight() - _offsetY,  // dest

          0, _offsetY, w, baseImage.getHeight(),  // src

          null);



      _compositeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);



      g.dispose();

      topG.dispose();

      bottomG.dispose();

    }  

      

      /**

       * create image to paint when hijacked, by painting the lower hald of the 

       * image offset by an amount determined by the animation. Then paint the

       * top part of the tree over the top, so some of the bottom peeks out.

       */

    private void _updateCompositeImage(){ 

      Graphics g = _compositeImage.getGraphics();  

      

      g.setColor(tree.getBackground());

      g.fillRect(0, 0, _compositeImage.getWidth(), _compositeImage.getHeight());

      

      int yOff = (int)(((float)_subTreeHeight) * (_animationComplete));

      if (_animationState == AnimatedTreeUI.AnimationState.COLLAPSING)

        yOff = _subTreeHeight - yOff;

      

      int dy1 = _offsetY - yOff;

      g.drawImage(_bottomImage, 0, dy1, null);

      g.drawImage(_topImage, 0, 0, null);

         

      g.dispose();  

    }



    private boolean _isAnimationComplete(){

      switch(_animationState){

        case COLLAPSING:

        case EXPANDING:

          return _animationComplete * _offsetY < 1.3f;

      }

      return true;

    }

    

    /**

     * get the height of the sub tree by measuring from the location of the first

     * child in the subtree to the bottom of its last sibling

     * The sub tree should be expanded for this to work correctly.

     */

    public int _getSubTreeHeight(TreePath path)

    {

      if (path.getParentPath() == null)

        return 0;

              

      Object origObj = path.getLastPathComponent();

      if (getModel().getChildCount(origObj) == 0)

        return 0;

        

      Object firstChild = getModel().getChild(origObj, 0);

      Object lastChild = getModel().getChild(origObj, getModel().getChildCount(origObj)-1);

      

      TreePath firstPath = path.pathByAddingChild(firstChild);    

      TreePath lastPath = path.pathByAddingChild(lastChild);

    

      int topFirst = getPathBounds(tree, firstPath).y;

      int bottomLast = getPathBounds(tree, lastPath).y + getPathBounds(tree, lastPath).height;

      

      int height = bottomLast - topFirst;

      return height;

    }

    

    

    private class TimerListener implements ActionListener{

      public void actionPerformed(ActionEvent actionEvent) {

        _animationComplete *= ANIMATION_SPEED;

        

        if (_isAnimationComplete()){

          _animationState = AnimatedTreeUI.AnimationState.NOT_ANIMATING;

          _timer.stop();

        }

        else {

          _updateCompositeImage();

        }

        tree.repaint();

      }

    }



    // overridden because the default clipping routine gives a NPE

    // when the painting is hijacked.

    public void update(Graphics g, JComponent c) {

        if (c.isOpaque()) {

            g.setColor(c.getBackground());

            g.fillRect(0, 0, c.getWidth(), c.getHeight());

        }



        if (_animationState != AnimatedTreeUI.AnimationState.NOT_ANIMATING) {

            if (c.getParent() instanceof JViewport){

                JViewport vp = (JViewport)c.getParent();

                Rectangle visibleR = vp.getViewRect();

                g.setClip(

                    visibleR.x, 

                    visibleR.y,

                    visibleR.width,

                    visibleR.height);

            } else {

                g.setClip(0, 0,

                _compositeImage.getWidth(),

                _compositeImage.getHeight());

            }

        }

        paint(g, c);

    }



    // Hijack painting when animating

    public void paint(Graphics g, JComponent c) {

        if (_animationState != AnimatedTreeUI.AnimationState.NOT_ANIMATING){

          g.drawImage(_compositeImage,

              0,

              0,

              null);

              return;

        }

        else

          super.paint(g, c);

    }

}





