package br.com.zynger.android.cloudstickynotes.model;

public class Note {
	public enum Color {
		YELLOW, BLUE, PURPLE, GREEN, RED;
	}
	
    public static class Size {
      private int height, width;

      public int getHeight() { return height; }
      public int getWidth() { return width; }

      public void setHeight(int h) { height = h; }
      public void setWidth(int w) { width = w; }
      public void setSize(int h, int w) {
    	  height = h;
    	  width = w;
      }
	
		@Override
		public String toString() {
			return "Size [height=" + height + ", width=" + width + "]";
		}
    }
    
    public static class Location {
        private int _x, _y;
        
        public int getX() { return _x; }
        public int getY() { return _y; }

        public void setX(int x) { _x = x; }
        public void setY(int y) { _y = y; }
        public void setLocation(int x, int y) {
        	_x = x;
        	_y = y;
        }
		
        
        @Override
		public String toString() {
			return "Location [x=" + _x + ", y=" + _y + "]";
		}
      }

    private String id;
    private Color color;
    private Size size;
    private String text;
    private Location location;
    
	public String getId() { return id; }
	public Color getColor() { return color; }
	public Size getSize() { return size; }
	public String getText() { return text; }
	public Location getLocation() { return location; }
	
	public void setId(String id) { this.id = id; }
	public void setColor(Color color) { this.color = color; }
	public void setSize(Size size) { this.size = size; }
	public void setText(String text) { this.text = text; }
	public void setLocation(Location location) { this.location = location; }
	
	@Override
	public String toString() {
		return "Note [id=" + id + ", color=" + color + ", size=" + size
				+ ", text=" + text + ", location=" + location + "]";
	}
}