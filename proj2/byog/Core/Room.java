package byog.Core;

import java.util.List;

public class Room {
    Position leftBtm;
    Position upRight;
    public Room(Position left, Position up) {
        leftBtm = left;
        upRight = up;
    }
    public boolean isValid(List<Room> rooms) {
        if (rooms.isEmpty()) {
            return true;
        } else {
            for (Room room : rooms) {
                boolean b1 = Math.min(upRight.x, room.upRight.x)
                        >= Math.max(leftBtm.x, room.leftBtm.x);
                boolean b2 = Math.min(upRight.y, room.upRight.y)
                        >= Math.max(leftBtm.y, room.leftBtm.y);
                if (b1 && b2) {
                    return false;
                }
            }
        }
        return true;
    }
}
