//
//  PurchaseViewController.swift
//  CharityMarket
//
//  Created by Zak Wegweiser on 10/6/18.
//  Copyright © 2018 Zak Wegweiser. All rights reserved.
//

import UIKit
import Firebase

class PurchaseViewController : UIViewController, UICollectionViewDelegate, UICollectionViewDataSource
{
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return data.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "charityCell", for: indexPath)
        
        cell.subviews.forEach({ $0.removeFromSuperview() })
        
        let imageView = UIImageView(frame: cell.bounds)
        cell.addSubview(imageView)
        imageView.downloadImageFrom(link: data[indexPath.row].imageURL, contentMode: .scaleAspectFill)
        
        let cellLabel = UILabel(frame: CGRect(x: 0, y: cell.bounds.size.height - 40, width: cell.bounds.size.width, height: 40))
        //cell.addSubview(cellLabel)
        
        cellLabel.textAlignment = .center
        cellLabel.adjustsFontSizeToFitWidth = true
        cellLabel.backgroundColor = UIColor.black.withAlphaComponent(0.75)
        cellLabel.text = data[indexPath.row].name
        cellLabel.textColor = .white
        
        cell.contentView.backgroundColor = nil
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cell = collectionView.cellForItem(at: indexPath)
        cell?.contentView.backgroundColor = UIColor.green.withAlphaComponent(0.4)
    }
    
    var marketObject : MarketObject!
    @IBOutlet var productImageView : UIImageView!
    @IBOutlet var nameLabel : UILabel!
    @IBOutlet var priceLabel : UILabel!
    @IBOutlet var descriptionTextView : UITextView!
    @IBOutlet var purchaseButton : UIButton!
    @IBOutlet var collectionView : UICollectionView!
    private var listener : ListenerRegistration!
    var data : [CharityObject] = []
    
    fileprivate func baseQuery() -> Query {
        return Firestore.firestore().collection("charities").limit(to: 50)
    }
    
    fileprivate var query: Query? {
        didSet {
            if let listener = listener {
                listener.remove()
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        productImageView.downloadImageFrom(link: marketObject.imageURL, contentMode: .scaleAspectFit)
        nameLabel.text = marketObject.name
        priceLabel.text = "Total: $\(marketObject.price).00"
        descriptionTextView.text = marketObject.descr
        purchaseButton.setTitle("Purchase", for: .normal)
        
        self.title = "Purchase \(marketObject.name)"
        
        let layout = ZoomAndSnapFlowLayout()
        layout.itemSize = CGSize(width: 160, height: 100)
        layout.scrollDirection = .horizontal
        
        collectionView.collectionViewLayout = layout
        collectionView.delegate = self
        collectionView.dataSource = self
        
        self.query = baseQuery()
        self.loadItems()
    }
    
    func loadItems() {
        self.listener =  query?.addSnapshotListener { (documents, error) in
            guard let snapshot = documents else {
                print("Error fetching documents results: \(error!)")
                return
            }
            
            let results = snapshot.documents.map { (document) -> CharityObject in
                let valueDictionary = document.data()
                let imageURL = valueDictionary["icon"] as! String
                let name = valueDictionary["name"] as! String
                return CharityObject(name: name, imageURL: imageURL)
            }
            
            self.data = results
            
            self.collectionView.reloadData()
        }
        
    }
    
    
    @IBAction func purchase()
    {
        self.dismiss(animated: true, completion: nil)
    }
    
}

struct CharityObject {
    var name : String
    var imageURL : String
}
