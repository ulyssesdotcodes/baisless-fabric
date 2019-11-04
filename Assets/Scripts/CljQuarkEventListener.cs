using UnityEngine;
using System.Collections.Generic;

public class CljQuarkEventListener: QuarkEventListener {
  public Queue<QuarkEvent> EventQueue = new Queue<QuarkEvent>();

  public override int Id {
    get { return -1; }
  }

  public override void OnEvent(QuarkEvent e) {
    EventQueue.Enqueue(e);
  }
}
