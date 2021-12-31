package code.Gameplay.Map;

import java.util.Vector;

/**
 *
 * @author DDDENISSS
 */
public class Dijkstra {
    
    private byte[][] ways;
    
    public Dijkstra(House home) {
        ways = calcWays(home);
    }

    public int getNext(int start, int finish) {
        return getPrevious(finish, start);
    }
    public int getPrevious(int start, int finish) {
        return ways[start][finish];
    }
    

    private static byte[][] calcWays(House home) {
        Room[] rooms = home.getRooms();

        Vector[] edges = new Vector[rooms.length];
        for (int y = 0; y < rooms.length; y++) {
            edges[y] = new Vector();
            for (int x = 0; x < rooms.length; x++) {
                if(y != x && home.isNear(x, y)) {
                    int dis = dis(rooms[x], rooms[y]);
                    edges[y].addElement( new Edge(x, dis) );
                }
            }
        }

        byte[][] ways = new byte[rooms.length][rooms.length];

        int[] prio = new int[rooms.length];
        int[] pred = new int[rooms.length];

        for (int y = 0; y < ways.length; y++) {
            //РёС‰РµРј РІСЃРµ РїСѓС‚Рё РѕС‚ РєРѕРјРЅР°С‚С‹ РЅРѕРјРµСЂ y РґРѕ РІСЃРµС… РѕСЃС‚Р°Р»СЊРЅС‹С…
            Dijkstra.shortestPaths(edges, y, prio, pred);
            for (int x = 0; x < ways.length; x++) {
                ways[y][x] = (byte) pred[x];
            }
        }
        
        return ways;
    }
    private static int dis(Room r1, Room r2) {
        Point p1 = new Point( (r1.getMinX()+r1.getMaxX())/2, (r1.getMinZ()+r1.getMaxZ())/2 );
        Point p2 = new Point( (r2.getMinX()+r2.getMaxX())/2, (r2.getMinZ()+r2.getMaxZ())/2 );
        Point d = new Point(p2.x-p1.x, p2.y-p1.y);
        return (int)Math.sqrt( (double)d.x*d.x + d.y*d.y );
    }

    private static void shortestPaths(Vector[] edges, int start, int[] prio, int[] pred) {
        for (int i = 0; i < prio.length; i++) {
            pred[i] = -1;
            prio[i] = Integer.MAX_VALUE;
        }

        prio[start] = 0;
        Vector q = new Vector();
        q.addElement(new QItem(0, start));
        while (!q.isEmpty()) {
            QItem cur = poll(q);
            if (cur.dis != prio[cur.u]) continue;
            for (int i=0; i<edges[cur.u].size(); i++) {
                Edge e = (Edge) edges[cur.u].elementAt(i);
                int v = e.t;
                int nprio = prio[cur.u] + e.cost;
                if (prio[v] > nprio) {
                    prio[v] = nprio;
                    pred[v] = cur.u;
                    q.addElement(new QItem(nprio, v));
                }
            }
        }
    }
    private static QItem poll(Vector items) {
        QItem min = (QItem) items.elementAt(0);
        for (int i = 1; i < items.size(); i++) {
            QItem t = (QItem) items.elementAt(i);
            if(t.dis < min.dis) min = t;
        }
        items.removeElement(min);
        return min;
    }
    

}


class Point {
    int x, y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    int dis(Point p) {
        int dx = x-p.x;
        int dy = y-p.y;
        return (int)Math.sqrt( dx*dx + dy*dy );
    }
}
class Edge {
    int t, cost;
    public Edge(int a, int b) {
        this.t = a;
        this.cost = b;
    }
}

class QItem {
    int dis; //СЂР°СЃСЃС‚РѕСЏРЅРёРµ РґРѕ СЃС‚Р°СЂС‚Р°
    int u;
    public QItem(int dis, int u) {
        this.dis = dis;
        this.u = u;
    }
}
