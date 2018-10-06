//
//  ViewController.swift
//  CharityMarket
//
//  Created by Zak Wegweiser on 10/5/18.
//  Copyright © 2018 Zak Wegweiser. All rights reserved.
//

import UIKit
import Firebase

class MarketPlaceViewController: UIViewController, UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource {
    
    var data : [[MarketObject]] = [[]]
    var categories : [String] = []
    private var listener : ListenerRegistration!
    @IBOutlet var searchBar : UISearchBar!
    @IBOutlet var tableView : UITableView!
    var storedOffsets = [Int: CGFloat]()
    
    fileprivate func baseQuery() -> Query {
        return Firestore.firestore().collection("listings").limit(to: 50)
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
        // Do any additional setup after loading the view, typically from a nib.
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(TableViewCell.self, forCellReuseIdentifier: "Cell")
        
        // Set up Search Bar
        searchBar.searchBarStyle = UISearchBar.Style.prominent
        searchBar.placeholder = " Search..."
        searchBar.sizeToFit()
        searchBar.isTranslucent = false
        searchBar.backgroundImage = UIImage()
        searchBar.delegate = self
        
        self.query = baseQuery()
        self.loadItems()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.listener.remove()
    }
    
    func loadItems() {
        self.listener =  query?.addSnapshotListener { (documents, error) in
            guard let snapshot = documents else {
                print("Error fetching documents results: \(error!)")
                return
            }
            
            let results = snapshot.documents.map { (document) -> MarketObject in
                let valueDictionary = document.data()
                let imageURL = valueDictionary["image"] as! String
                let name = valueDictionary["title"] as! String
                let price = valueDictionary["price"] as! String
                let tags = valueDictionary["tags"] as! String
                let description = valueDictionary["description"] as! String
                let seller_charity = valueDictionary["sellerNGO"] as! String
                let brand = valueDictionary["brand"] as! String
                let seller_uid = valueDictionary["seller_uid"] as! String
                return MarketObject(imageURL: imageURL, name: name, price: price, tags: tags, brand: brand, description: description, seller_uid: seller_uid, seller_charity: seller_charity)
            }
            
            var categories: [String : [MarketObject]] = [:]
            for result in results {
                let tag = result.tags
                let charity = result.seller_charity
                if categories[charity] == nil {
                    categories[charity] = []
                }
                categories[charity]!.append(result)
            }
            
            self.data = Array(categories.values)
            self.categories = Array(categories.keys)
            
            self.tableView.reloadData()
        }
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return categories.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return categories[section]
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let tableViewCell = cell as? TableViewCell else { return }
        
        tableViewCell.setCollectionViewDataSourceDelegate(self, forSection: indexPath.section)
        tableViewCell.collectionViewOffset = storedOffsets[indexPath.row] ?? 0
    }
    
    func tableView(_ tableView: UITableView, didEndDisplaying cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        
        guard let tableViewCell = cell as? TableViewCell else { return }
        
        storedOffsets[indexPath.row] = tableViewCell.collectionViewOffset
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 120
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
    }
}

extension MarketPlaceViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return data[collectionView.tag].count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "collectionViewCell", for: indexPath) as! CollectionViewCell
        
        let marketItem = data[collectionView.tag][indexPath.row]
        let cellImageURL = marketItem.imageURL
        cell.cellImageView.downloadImageFrom(link: cellImageURL, contentMode: UIView.ContentMode.scaleAspectFill)
        cell.cellLabel.text = marketItem.name + " : $" + marketItem.price + ".00"
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        print("Collection view at row \(collectionView.tag) selected index path \(indexPath)")
    }
}
