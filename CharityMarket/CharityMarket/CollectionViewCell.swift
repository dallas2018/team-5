//
//  CollectionViewCell.swift
//  CharityMarket
//
//  Created by Zak Wegweiser on 10/5/18.
//  Copyright © 2018 Zak Wegweiser. All rights reserved.
//

import UIKit

class CollectionViewCell: UICollectionViewCell {
    var cellImageView = UIImageView()
    var cellLabel = UILabel()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        // Add imageView to contentView
        contentView.addSubview(cellImageView)
        cellImageView.translatesAutoresizingMaskIntoConstraints = false
        cellImageView.leftAnchor.constraint(equalTo: contentView.leftAnchor).isActive = true
        cellImageView.topAnchor.constraint(equalTo: contentView.topAnchor).isActive = true
        cellImageView.rightAnchor.constraint(equalTo: contentView.rightAnchor).isActive = true
        cellImageView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor).isActive = true
        
        cellImageView.contentMode = .scaleAspectFit
        cellImageView.image = UIImage(named: "placeholder")
        
        // Add cellLabel to contentView
        contentView.addSubview(cellLabel)
        cellLabel.translatesAutoresizingMaskIntoConstraints = false
        cellLabel.leftAnchor.constraint(equalTo: contentView.leftAnchor).isActive = true
        cellLabel.rightAnchor.constraint(equalTo: contentView.rightAnchor).isActive = true
        cellLabel.bottomAnchor.constraint(equalTo: contentView.bottomAnchor).isActive = true
        
        cellLabel.font = UIFont(name: "Roboto", size: 18.0)
        cellLabel.textColor = UIColor.black
        cellLabel.text = "Loading..."
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

extension UIImageView {
    func downloadImageFrom(link:String, contentMode: UIView.ContentMode) {
        URLSession.shared.dataTask( with: NSURL(string:link)! as URL, completionHandler: {
            (data, response, error) -> Void in
            DispatchQueue.main.async {
                self.contentMode =  contentMode
                if let data = data { self.image = UIImage(data: data) }
            }
        }).resume()
    }
}
